setup:
	@export DOCKER_BUILDKIT=0
	@export COMPOSE_DOCKER_CLI_BUILD=0

test-all:
	@yarn --cwd ./frontend test
	@./backend/gradlew -p backend test
	@./backend/gradlew -p backend integrationTest
	@./backend/gradlew -p backend componentTest

build-backend:
	@./backend/gradlew -p backend build

build-container-backend:
	@docker build backend -f backend/Dockerfile -t sales-importer:latest

build-container-frontend:
	@docker build frontend -f frontend/Dockerfile -t sales-importer-frontend:latest

verify: test-all
	@yarn --cwd ./frontend lint
	@./backend/gradlew -p backend ktlintCheck

format:
	@yarn --cwd ./frontend format
	@./backend/gradlew -p backend ktlintFormat

up: setup
	@docker compose -f docker-compose.yml up --build

down: setup
	@docker compose -f docker-compose.yml down --remove-orphans

run-database:
	@docker run -d -p 5432:5432 --env POSTGRES_PASSWORD=simplepassword --env POSTGRES_DB=sales --name database-sales-importer postgres:15.2-alpine

start-database:
	@docker start database-sales-importer

stop-database:
	@docker stop database-sales-importer

clean:
	@./backend/gradlew -p backend clean

.PHONY: up