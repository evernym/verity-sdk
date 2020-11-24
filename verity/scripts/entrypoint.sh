#!/bin/bash

set -o pipefail

ANSII_GREEN='\u001b[32m'
ANSII_RESET='\x1b[0m'

# Handle ctrl-C to exit the application
trap_ctrlC() {
    if [ -n "$NGROK_PID" ]; then
        kill "$NGROK_PID"
    fi
    exit 1
}
trap trap_ctrlC SIGINT SIGTERM

provision() {
  echo
  echo "***************************   PROVISIONING    ************************"

  # If seed was not defined generate a random one
  if [ -z "$VERITY_SEED" ]; then
    VERITY_SEED=$(date +%s | md5sum | base64 | head -c 32)
  fi

  # If network was not defined default to "demo"
  if [ -z "$NETWORK" ]; then
    NETWORK="demo"
    TXN_FILE="demo.txn"
  fi

  # Prepare genesis file and TAA data for the selected network
  case "$NETWORK" in
    demo)
      export TXN_FILE="demo.txn"
      DATE=$(date +%F)
      export TAA_ACCEPTANCE=$DATE
      export TAA_HASH="8cee5d7a573e4893b08ff53a0761a22a1607df3b3fcd7e75b98696c92879641f"
      export TAA_VERSION="2.0"
      shift
      ;;
    prod)
      export TXN_FILE="prod.txn"
      DATE=$(date +%F)
      export TAA_ACCEPTANCE=$DATE
      export TAA_HASH="8cee5d7a573e4893b08ff53a0761a22a1607df3b3fcd7e75b98696c92879641f"
      export TAA_VERSION="2.0"
      shift
      ;;
    team1)
      export TXN_FILE="team1.txn"
      DATE=$(date +%F)
      export TAA_ACCEPTANCE=$DATE
      export TAA_HASH="3ae97ea501bd26b81c8c63da2c99696608517d6df8599210c7edaa7e2c719d65"
      export TAA_VERSION="1.0.0"
      shift
      ;;
    *)
      echo "ERROR: Unknown NETWORK $NETWORK. Must be one of \"demo\", \"prod\" or \"team1\""
      print_usage_and_exit
      shift
      ;;
  esac

  # Create DID/Verkey based on the provided VERITY_SEED 
  printf "wallet create test key=test\nwallet open test key=test\ndid new seed=%s" "${VERITY_SEED}" | indy-cli | tee /tmp/indy_cli_output.txt
  echo

  if [ "$NETWORK" == "demo" ] ; then
    # Register Verity DID/Verkey on the ledger if the network is "demo" via Sovrin Self Serve portal
    DID=`grep verkey /tmp/indy_cli_output.txt |awk '{print $2}'`
    VERKEY=`grep verkey /tmp/indy_cli_output.txt |awk '{print $7}'`
    echo "Registering DID ${DID} and VerKey ${VERKEY} on Sovrin Staging Net via Sovrin SelfServe portal"
    echo "----Response Begin----"
    curl -sd "{\"network\":\"stagingnet\",\"did\":$DID,\"verkey\":$VERKEY,\"paymentaddr\":\"\"}" https://selfserve.sovrin.org/nym
    echo
    echo "----Response End----"
  else
    # For other networks wait for the user to register DID/Verkey manually
    echo "Add the DID and Verkey to the target environment"
    read -p "Press enter to continue..."
  fi

  # Write out TAA configruation to the file
  echo "verity.lib-indy.ledger.transaction_author_agreement.agreements = {\"${TAA_VERSION}\" = { digest = \${?TAA_HASH}, mechanism = on_file, time-of-acceptance = \${?TAA_ACCEPTANCE}}}" > /etc/verity/verity-application/taa.conf

  # Generate random logo
  ROBO_HASH=$(date +%s | md5sum | base64 | head -c 8)
  export LOGO_URL="http://robohash.org/${ROBO_HASH}"
  echo "**********************************************************************"
  echo

}

start_ngrok() {
  ngrok http 9000 >> /dev/null &
  NGROK_PID=$!
  until curl -m 1 -q http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r -e '.tunnels[0].public_url' > /dev/null 2>&1
  do
    echo -n "."
    sleep 1
  done
}

save_env() {
  echo "export VERITY_SEED=$VERITY_SEED" >> provisioned.conf
  echo "export NETWORK=$NETWORK" >> provisioned.conf
  echo "export TXN_FILE=${TXN_FILE}" >> provisioned.conf
  echo "export TAA_VERSION=${TAA_VERSION}" >> provisioned.conf
  echo "export TAA_HASH=${TAA_HASH}" >> provisioned.conf
  echo "export TAA_ACCEPTANCE=${TAA_ACCEPTANCE}" >> provisioned.conf
  echo "export LOGO_URL=${LOGO_URL}" >> provisioned.conf
  echo "export HOST_ADDRESS=${HOST_ADDRESS}" >> provisioned.conf
}

print_config() {
  echo "******************        VERITY PARAMETERS         ******************"
  echo "VERITY_SEED=$VERITY_SEED"
  echo "NETWORK=$NETWORK"
  echo "TXN_FILE=${TXN_FILE}"
  echo "TAA_VERSION=${TAA_VERSION}"
  echo "TAA_HASH=${TAA_HASH}"
  echo "TAA_ACCEPTANCE=${TAA_ACCEPTANCE}"
  echo "LOGO_URL=${LOGO_URL}"
  echo "HOST_ADDRESS=${HOST_ADDRESS}"
  echo "**********************************************************************"
  echo
}

print_license() {
  echo
  echo "***********************        LICENSE         ***********************"
  echo "Verity application is available under the Business Source License"
  printf "${ANSII_GREEN}https://github.com/evernym/verity/blob/master/LICENSE.txt${ANSII_RESET}\n"
  echo "Please contact Evernym if you have any questions regarding Verity licensing"
  echo "**********************************************************************"
  echo
}

start_verity() {

  print_license

  if [ -f provisioned.conf ]; then
    # Provisioning file exists. This is start of the stopped Verity container
    # Source environment variables from the previous run
    . provisioned.conf
    export BOOTSTRAP="done"
  else
    # First time strart. Do the provisioning
    provision
    save_env
  fi

  print_config

  echo "**********************       VERITY STARTUP         ******************"
  # If public URL for docker Host is not specified start Ngrok tunnel to obtain public Verity Application endpoint
  if [ -z "$HOST_ADDRESS" ]; then
    echo "No HOST_ADDRESS specified"
    echo -n Starting ngrok..
    start_ngrok
    export HOST_ADDRESS=$(curl -m 1 -s http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r '.tunnels[0].public_url')
  fi

  export HOST_DOMAIN=`echo $HOST_ADDRESS |  cut -d'/' -f3`

  echo
  printf "Verity Endpoint is: ${ANSII_GREEN}${HOST_ADDRESS}${ANSII_RESET}"
  echo
  echo

  # Start Verity Application
  /usr/bin/java -cp /etc/verity/verity-application:.m2/repository/org/fusesource/leveldbjni/leveldbjni-all/1.8/leveldbjni-all-1.8.jar:/usr/lib/verity-application/verity-application-assembly.jar \
  com.evernym.verity.Main &> log.txt &

  echo
  echo -n "Waiting for Verity to start listening."
  until curl -q 127.0.0.1:9000/agency > /dev/null 2>&1
  do
      echo -n "."
      sleep 1
  done
  echo

  # Bootstrap Verity Application with seed
  if [ -z "$BOOTSTRAP" ]; then
    echo "Bootstrapping Verity"
    echo
    echo "Verity Setup"
    curl -f -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/agency/internal/setup/key \
    -d "{\"seed\":\"$VERITY_SEED\"}" || exit 1
    echo; echo
    echo "Verity Endpoint Setup"
    curl -f -X POST http://127.0.0.1:9000/agency/internal/setup/endpoint || exit 1
    echo; echo
    echo "Verity Bootstrapping complete."
  fi

  echo "Verity application started."
  echo "**********************************************************************"
  echo

  read -p "Press enter to start tailing log..."
  tail -f -n +1 log.txt
}

start_verity
