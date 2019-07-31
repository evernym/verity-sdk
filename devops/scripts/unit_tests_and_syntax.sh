#!/bin/bash

set -ex

cd sdk/java-sdk/ && mvn pmd:check && mvn pmd:cpd-check && cd - # Java SDK linting
cd sdk/java-sdk/ && mvn test && cd - # Java SDK unit tests
cd sdk/python-sdk/ && make lint && cd - # Python SDK linting
cd sdk/python-sdk/ && pytest && cd - # Python SDK unit tests
cd server && npm run lint && cd - # Mock Verity linting

