-- ============================================
-- first-logistics 데이터베이스 + 스키마 초기화
-- ============================================

-- 확장 설치
CREATE EXTENSION IF NOT EXISTS vector SCHEMA public;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp" SCHEMA public;

-- Master DB 스키마
CREATE SCHEMA IF NOT EXISTS delivery;
CREATE SCHEMA IF NOT EXISTS orders;
CREATE SCHEMA IF NOT EXISTS hub;
CREATE SCHEMA IF NOT EXISTS company;
CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS notification;
CREATE SCHEMA IF NOT EXISTS ai;
CREATE SCHEMA IF NOT EXISTS sample;

-- Master DB Publication (Logical Replication)
CREATE PUBLICATION master_pub FOR ALL TABLES;

-- Slave DB 생성
CREATE DATABASE "first-logistics-slave";

\connect "first-logistics-slave";

CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA IF NOT EXISTS delivery;
CREATE SCHEMA IF NOT EXISTS orders;
CREATE SCHEMA IF NOT EXISTS hub;
CREATE SCHEMA IF NOT EXISTS company;
CREATE SCHEMA IF NOT EXISTS product;
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS notification;
CREATE SCHEMA IF NOT EXISTS ai;
CREATE SCHEMA IF NOT EXISTS sample;

-- Slave DB Subscription (Master DB 구독)
CREATE SUBSCRIPTION slave_sub
    CONNECTION 'host=localhost port=5432 dbname=first-logistics user=postgres'
    PUBLICATION master_pub
    WITH (copy_data = true, enabled = true);
