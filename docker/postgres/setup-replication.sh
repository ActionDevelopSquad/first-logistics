#!/bin/bash
# PostgreSQL 완전 기동 후 Logical Replication Subscription 설정

set -e

echo "Waiting for PostgreSQL to be ready..."
until pg_isready -h postgres -U postgres; do
  sleep 2
done

echo "Checking if subscription already exists..."
EXISTS=$(psql -h postgres -U postgres -d first-logistics-slave -tAc \
  "SELECT 1 FROM pg_subscription WHERE subname = 'slave_sub'" 2>/dev/null || echo "0")

if [ "$EXISTS" = "1" ]; then
  echo "Subscription 'slave_sub' already exists. Skipping."
else
  echo "Creating subscription..."
  psql -h postgres -U postgres -d first-logistics-slave -c "
    CREATE SUBSCRIPTION slave_sub
      CONNECTION 'host=postgres port=5432 dbname=first-logistics user=postgres'
      PUBLICATION master_pub
      WITH (copy_data = true, enabled = true);
  "
  echo "Subscription created successfully."
fi
