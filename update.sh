#!/usr/bin/env bash

source variables.env

#mkdir -p cache/gradle
#mkdir -p cache/vaadin

git pull
docker-compose build
docker-compose down
docker-compose up -d
docker cp eng_produtoh:/gradle cache
docker cp eng_produtoh:/vaadin cache

date
