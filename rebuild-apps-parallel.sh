#!/bin/bash
# 앱 서비스 3개씩 병렬 빌드 + 재배포

set -e

echo "=== 앱 서비스 빌드 시작 (3개씩) ==="

echo "[1/4] eureka-server, user-service, hub-service"
docker compose build eureka-server user-service hub-service

echo "[2/4] company-service, product-service, order-service"
docker compose build company-service product-service order-service

echo "[3/4] delivery-service, notification-service, ai-service"
docker compose build delivery-service notification-service ai-service

echo "[4/4] api-gateway"
docker compose build api-gateway

echo "=== 앱 서비스 재배포 ==="
docker compose up -d eureka-server user-service hub-service company-service product-service order-service delivery-service notification-service ai-service api-gateway

echo "=== 완료 ==="
