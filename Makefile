setup:
	@export DOCKER_BUILDKIT=0
	@export COMPOSE_DOCKER_CLI_BUILD=0

test-all:
	@./backend/gradlew -p backend test
	@./backend/gradlew -p backend integrationTest
	@./backend/gradlew -p backend componentTest

build-backend:
	@./backend/gradlew -p backend build

verify: test-all
	@./backend/gradlew -p backend ktlintCheck

up: setup
	@docker compose -f docker-compose.yml up --build

down: setup
	@docker compose -f docker-compose.yml down --remove-orphans

clean:
	@./backend/gradlew -p backend clean

.PHONY: up