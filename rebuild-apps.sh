#!/bin/bash
# 앱 서비스만 순차 빌드 + 재배포 (인프라 유지)

set -e

SERVICES=(
  eureka-server
  user-service
  hub-service
  company-service
  product-service
  order-service
  delivery-service
  notification-service
  ai-service
  api-gateway
)

echo "=== 앱 서비스 순차 빌드 시작 ==="
for svc in "${SERVICES[@]}"; do
  echo "Building $svc..."
  docker compose build "$svc"
done

echo "=== 앱 서비스 재배포 ==="
docker compose up -d "${SERVICES[@]}"

echo "=== 완료 ==="
