#!/usr/bin/env bash
mkdir -p cache/gradle
mkdir -p cache/vaadin

git pull
docker-compose build
docker-compose down
docker-compose up -d --build
docker cp eng_substi:/gradle cache
docker cp eng_substi:/vaadin cache
