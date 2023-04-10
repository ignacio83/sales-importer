setup:
	@export DOCKER_BUILDKIT=0
	@export COMPOSE_DOCKER_CLI_BUILD=0

up: setup
	docker compose -f docker-compose.yml up

down: setup
	docker compose -f docker-compose.yml down --remove-orphans

.PHONY: up