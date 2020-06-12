#!/bin/bash

set -o pipefail

RED='\033[0;31m'
NC='\033[0m' # No Color

# Handle ctrl-C to exit the application
trap_ctrlC() {
    if [ -n "$NGROK_PID" ]; then
        kill $NGROK_PID
    fi
    exit 1
}
trap trap_ctrlC SIGINT SIGTERM

echo "******************            PARAMETERS            ******************"
if [ -z "$VERITY_SEED" ]; then
    VERITY_SEED=$(date +%s | md5sum | base64 | head -c 32)
fi

# Set NETWORK params
if [ -z "$NETWORK" ]; then
  NETWORK="demo"
  TXN_FILE="demo.txn"
else
  case "$NETWORK" in
    demo)
      export TXN_FILE="demo.txn"
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
    stage)
      export TXN_FILE="stage.txn"
      DATE=$(date +%F)
      export TAA_ACCEPTANCE=$DATE
      export TAA_HASH="8cee5d7a573e4893b08ff53a0761a22a1607df3b3fcd7e75b98696c92879641f"
      export TAA_VERSION="2.0"
      shift
      ;;
    *)
      echo "ERROR: Unknown NETWORK $NETWORK. Must be one of \"demo\" or \"team1\""
      print_usage_and_exit
      shift
      ;;
  esac
fi

echo
echo "VERITY_SEED: ${VERITY_SEED}"
echo "NETWORK: ${NETWORK}"
echo "TXN_FILE: ${TXN_FILE}"
echo "TAA_VERSION: ${TAA_VERSION}"
echo "TAA_HASH: ${TAA_HASH}"
echo "TAA_ACCEPTANCE: ${TAA_ACCEPTANCE}"
echo

echo "**************************                  **************************"
echo
echo "******************   GENERATE VERITY AGENCY DID     ******************"
printf "wallet create test key=test\nwallet open test key=test\ndid new seed=%s" "${VERITY_SEED}" | indy-cli

# TODO for some networks we could automate this step
# curl 'https://selfserve.sovrin.org/nym' --data '{"network":"<NETWORK>","did":"<DID>","verkey":"<VERKEY>","paymentaddr":""}'

echo
echo "Add the DID and Verkey to the target environment"
read -p "Press enter to continue..."
echo "**************************     COMPLETE     **************************"
sleep 1
echo
echo "****************** GETTING START VERITY APPLICATION ******************"
# Start ngrok
echo -n Starting ngrok..
ngrok http 9000 >> /dev/null &
NGROK_PID=$!

until curl -m 1 -q http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r -e '.tunnels[0].public_url' > /dev/null 2>&1
do
  echo -n "."
  sleep 1
done
NGROK_HOST="$(curl -m 1 -s http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r '.tunnels[0].public_url' | cut -d'/' -f3)"
export NGROK_HOST


echo
printf "Verity Endpoint: ${RED}http://${NGROK_HOST}${NC}"
echo

# Write out TAA configruation to file
echo "agency.lib-indy.ledger.transaction_author_agreement.agreements = {\"${TAA_VERSION}\" = { digest = \${?TAA_HASH}, mechanism = on_file, time-of-acceptance = \${?TAA_ACCEPTANCE}}}" > /etc/verity/verity-application/taa.conf

ROBO_HASH=$(date +%s | md5sum | base64 | head -c 8)
export LOGO_URL="http://robohash.org/${ROBO_HASH}"



# Start Verity Application
/usr/bin/java -cp /etc/verity/verity-application:/usr/lib/verity-application/verity-application-assembly.jar \
com.evernym.verity.Main &> log.txt &

# Bootstrap Verity Application with seed
echo
echo -n "Waiting for Verity to start listening."
until curl -q 127.0.0.1:9000/agency > /dev/null 2>&1
do
    echo -n "."
    sleep 1
done
echo

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

echo "**************************     COMPLETE     **************************"
echo
echo "**************************       LOG        **************************"
read -p "Press enter to start tailing log..."
tail -f -n +1 log.txt
