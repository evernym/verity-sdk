#!/bin/bash

docker-compose rm -sf # Force remove all running containers to make sure old containers aren't being reused.
docker-compose build
docker-compose run --rm --name integration-tests java-integration-tests
docker-compose rm -sf # Cleanup
