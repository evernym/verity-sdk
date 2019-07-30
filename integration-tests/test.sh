#!/bin/bash

docker-compose rm -sf # Force remove all running containers to make sure old containers aren't being reused.
docker-compose build
docker-compose run --rm --name integration-tests java-integration-tests
STATUS=$?
docker-compose rm -sf # Cleanup
exit $STATUS
