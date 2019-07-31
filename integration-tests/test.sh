#!/bin/bash

docker-compose rm -sf
docker-compose build
docker-compose run --rm --name integration-tests java-integration-tests
JAVA_STATUS=$?
docker-compose rm -sf
docker-compose run --rm --name integration-tests python-integration-tests
PYTHON_STATUS=$?
docker-compose rm -sf

! (( $JAVA_STATUS || $PYTHON_STATUS ))
