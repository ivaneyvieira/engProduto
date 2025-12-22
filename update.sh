#!/usr/bin/env bash

source variables.env

#git pull
DOCKER_BUILDKIT=1 COMPOSE_DOCKER_CLI_BUILD=1 docker-compose build

docker-compose stop
docker-compose up --force-recreate -d

# Verifica se o comando foi bem-sucedido
if [ $? -eq 0 ]; then
  echo "Containers started successfully"
else
  echo "Failed to start containers" >&2
  exit 1
fi


date
