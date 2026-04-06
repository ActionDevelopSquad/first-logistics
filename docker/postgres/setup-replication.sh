#!/bin/bash
# PostgreSQL Logical Replication 자동 설정 (같은 인스턴스 내)
# 실행 시점: 모든 서비스가 healthy (테이블 생성 완료) 후

set -e

MASTER_DB="first-logistics"
SLAVE_DB="first-logistics-slave"
PG_HOST="postgres"
PG_USER="postgres"

echo "=== Logical Replication 설정 시작 ==="

# 1. subscription 이미 존재하면 스킵
EXISTS=$(psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -tAc \
  "SELECT 1 FROM pg_subscription WHERE subname = 'slave_sub'" 2>/dev/null || echo "0")

if [ "$EXISTS" = "1" ]; then
  echo "Subscription 'slave_sub' already exists. Skipping."
  exit 0
fi

# 2. master 스키마를 slave로 복사 (테이블 구조만, 데이터 제외)
echo "Copying schema from master to slave..."
SCHEMAS="delivery orders hub company product users notification ai"

for SCHEMA in $SCHEMAS; do
  echo "  - $SCHEMA 스키마 복사 중..."
  pg_dump -h $PG_HOST -U $PG_USER -d $MASTER_DB \
    --schema=$SCHEMA --schema-only --no-owner --no-privileges \
    | psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -q 2>/dev/null || true
done

# public 스키마 (vector_store 등)
echo "  - public 스키마 복사 중..."
pg_dump -h $PG_HOST -U $PG_USER -d $MASTER_DB \
  --schema=public --schema-only --no-owner --no-privileges \
  -T 'public.databasechangelog*' \
  | psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -q 2>/dev/null || true

echo "Schema copy complete."

# 3. subscription 생성 (connect=false로 데드락 방지)
echo "Creating subscription (connect=false)..."
psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -c "
  CREATE SUBSCRIPTION slave_sub
    CONNECTION 'host=$PG_HOST port=5432 dbname=$MASTER_DB user=$PG_USER'
    PUBLICATION master_pub
    WITH (copy_data = false, enabled = false, connect = false);
"
echo "Subscription created."

# 4. replication slot 수동 생성 (master DB)
echo "Creating replication slot..."
psql -h $PG_HOST -U $PG_USER -d $MASTER_DB -c "
  SELECT pg_create_logical_replication_slot('slave_sub', 'pgoutput');
"
echo "Replication slot created."

# 5. subscription 활성화
echo "Enabling subscription..."
psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -c "
  ALTER SUBSCRIPTION slave_sub ENABLE;
"
echo "Subscription enabled."

# 6. publication refresh (테이블 매핑)
echo "Refreshing publication..."
psql -h $PG_HOST -U $PG_USER -d $SLAVE_DB -c "
  ALTER SUBSCRIPTION slave_sub REFRESH PUBLICATION;
"
echo "Publication refreshed."

echo "=== Logical Replication 설정 완료 ==="
