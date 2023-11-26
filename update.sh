#!/usr/bin/env bash

source variables.env

mkdir -p cache/gradle
mkdir -p cache/vaadin

git pull
docker-compose --env-file variables.env build
docker-compose --env-file variables.env down
docker-compose --env-file variables.env up -d --build
docker cp eng_produtoh:/gradle cache
docker cp eng_produtoh:/vaadin cache
