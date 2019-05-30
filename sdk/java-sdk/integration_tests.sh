#!/bin/bash
set -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
EXIT_CODE=0

function test_java_sdk() {
    cd $SCRIPT_DIR/example
    # Run the example app in background
    ./run.sh &
    sleep 15 # Wait for invitation details to be written to file
    cd $SCRIPT_DIR/../../tools/
    # Run client auto responder
    ./vcx-client-auto-respond.py $(cat $SCRIPT_DIR/example/inviteDetails.json)
}

# Build Java SDK and example
cd $SCRIPT_DIR
mvn package -DskipTests
cd $SCRIPT_DIR/example
mvn package

# Start Mock Verity
cd $SCRIPT_DIR/../../server
RUN npm config set strict-ssl=false # FIXME: We need to get rid of this!! Pull from real NPM!
npm install
npm run build
docker-compose up --build -d
sleep 30 # Wait for Mock Verity to come up

# Provision
rm -rf ~/.indy_client/wallet/test-wallet
cd $SCRIPT_DIR/../../tools/
./provision_sdk.py --wallet-name test-wallet http://localhost:8080 12345 > ../sdk/java-sdk/example/verityConfig.json

test_java_sdk || EXIT_CODE=1

docker kill verity-server
docker rm verity-server

exit $EXIT_CODE


