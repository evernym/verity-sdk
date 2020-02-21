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

print_usage_and_exit() {
    echo "usage: ./entrypoint.sh -s|--enterprise-seed ENTERPRISE_SEED -e|--environment ENVIRONMENT"
    echo
    echo "positional arguments:"
    echo -e "\t -s, --enterprise-seed\t(REQUIRED) seed for Verity's primary did and verkey"
    echo -e "\t -e, --environment\t one of \"demo\" or \"team1\". default: \"demo\""
    exit 1
}

PARAMS=""
while (( "$#" )); do
  case "$1" in
    -s|--enterprise-seed)
      ENTERPRISE_SEED=$2
      shift 2
      ;;
    -e|--environment)
      ENVIRONMENT=$2
      shift 2
      ;;
    -h|--help)
      print_usage_and_exit
      shift 1
      ;;
    --) # end argument parsing
      shift
      break
      ;;
    -*|--*=) # unsupported flags
      echo "Error: Unsupported flag $1" >&2
      exit 1
      ;;
    *) # preserve positional arguments
      PARAMS="$PARAMS $1"
      shift
      ;;
  esac
done

# set positional arguments in their proper place
eval set -- "$PARAMS"
# Validate arguments
if [ -z "$ENTERPRISE_SEED" ]; then
    ENTERPRISE_SEED=$(date +%s | md5sum | base64 | head -c 32)
fi

# Set ENVIRONMENT params
if [ -z "$ENVIRONMENT" ]; then
  TXN_FILE="demo.txn"
else
  case "$ENVIRONMENT" in
    demo)
      TXN_FILE="demo.txn"
      shift
      ;;
    team1)
      TXN_FILE="team1.txn"
      shift
      ;;
    *)
      echo "ERROR: Unknown ENVIRONMENT $ENVIRONMENT. Must be one of \"demo\" or \"team1\""
      print_usage_and_exit
      shift
      ;;
  esac
fi

echo "******************   GENERATE VERITY AGNECY DID     ******************"
printf "wallet create test key=test\nwallet open test key=test\ndid new seed=%s" "${ENTERPRISE_SEED}" | indy-cli

echo
echo "Add the DID and Verkey to the target environment"
read -p "Press enter to continue..."
echo "**************************     COMPLETE     **************************"
sleep 1
echo
echo "****************** GETTING START VERITY APPLICATION ******************"
# Start ngrok
echo -n Starting ngrok...
ngrok http 9000 >> /dev/null &
NGROK_PID=$!

until curl -m 1 -q http://127.0.0.1:4040/api/tunnels > /dev/null 2>&1
do
  echo -n "."
  sleep 1
done
NGROK_HOST="$(curl -m 1 -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url' | cut -d'/' -f3)"


echo
printf "Verity Endpoint: ${RED}http://${NGROK_HOST}${NC}"
echo

# Configure local ip address
APP_CONFIG_FILE='/etc/verity/verity-application/application.conf'
sed -i "s/NGROK_HOST/$NGROK_HOST/g" $APP_CONFIG_FILE

# Set environment in configuration
sed -i "s/TXN_FILE/$TXN_FILE/g" $APP_CONFIG_FILE

# Start Verity Application
/usr/bin/java -javaagent:/usr/lib/verity-application/aspectjweaver.jar \
-cp /etc/verity/verity-application:/usr/lib/verity-application/verity-application-assembly.jar \
com.evernym.verity.Main &> log.txt &

# Bootstrap Verity Application with seed
echo
echo -n "Waiting for Verity to start listening..."
until curl -q 127.0.0.1:9000/agency > /dev/null 2>&1
do
    echo -n "."
    sleep 1
done
echo

echo "Bootstrapping Verity"
echo

curl -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/agency/internal/setup/key \
-d "{\"seed\":\"$ENTERPRISE_SEED\"}" &> /dev/null
curl -X POST http://127.0.0.1:9000/agency/internal/setup/endpoint &> /dev/null

echo "Verity Bootstrapping complete."

echo "**************************     COMPLETE     **************************"
echo
echo "**************************       LOG        **************************"
read -p "Press enter to start tailing log..."
tail -f -n +1 log.txt