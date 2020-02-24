#!/bin/bash

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
      shift
      ;;
    team1)
      export TXN_FILE="team1.txn"
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
echo

echo "**************************                  **************************"
echo
echo "******************   GENERATE VERITY AGNECY DID     ******************"
printf "wallet create test key=test\nwallet open test key=test\ndid new seed=%s" "${VERITY_SEED}" | indy-cli

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

until curl -m 1 -q http://127.0.0.1:4040/api/tunnels > /dev/null 2>&1
do
  echo -n "."
  sleep 1
done
export NGROK_HOST="$(curl -m 1 -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url' | cut -d'/' -f3)"


echo
printf "Verity Endpoint: ${RED}http://${NGROK_HOST}${NC}"
echo

# Start Verity Application
/usr/bin/java -javaagent:/usr/lib/verity-application/aspectjweaver.jar \
-cp /etc/verity/verity-application:/usr/lib/verity-application/verity-application-assembly.jar \
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