#!/bin/sh

rm -fR cache

docker compose down

docker image ls | grep task1 | \
    awk '{ print $1":"$2; }' | \
    xargs docker image rm

docker image ls | grep process | \
    awk '{ print $1":"$2; }' | \
    xargs docker image rm