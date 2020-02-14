#!/bin/bash

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
    echo "ERROR: Missing ENTERPRISE_SEED"
    print_usage_and_exit
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

# Start ngrok
ngrok http 9000 > /dev/null &
sleep 5 # Wait for ngrok to startup
NGROK_PID=$!
NGROK_HOST="$(curl -s http://127.0.0.1:4040/api/tunnels | jq -r '.tunnels[0].public_url' | cut -d'/' -f3)"

# Handle ctrl-C to exit the application
trap_ctrlC() {
    kill $NGROK_PID
    exit 1
}
trap trap_ctrlC SIGINT SIGTERM

# Configure local ip address
APP_CONFIG_FILE='/etc/verity/verity-application/application.conf'
sed -i "s/NGROK_HOST/$NGROK_HOST/g" $APP_CONFIG_FILE

# Set environment in configuration
sed -i "s/TXN_FILE/$TXN_FILE/g" $APP_CONFIG_FILE

# Start Verity Application
/usr/bin/java -javaagent:/usr/lib/verity-application/aspectjweaver.jar -cp /etc/verity/verity-application:/usr/lib/verity-application/verity-application-assembly.jar com.evernym.verity.Main &

# Bootstrap Verity Application with seed
until curl -q 127.0.0.1:9000/agency > /dev/null 2>&1
do
    echo "Waiting for Verity to start listening..."
    sleep 2
done
echo "Bootstrapping Verity"
curl -H "Content-Type: application/json" -X POST http://127.0.0.1:9000/agency/internal/setup/key  -d "{\"seed\":\"$ENTERPRISE_SEED\"}"
curl -X POST http://127.0.0.1:9000/agency/internal/setup/endpoint

echo "Verity Bootstrapping complete. Listening on http://$NGROK_HOST"

# Wait for Verity Application to exit (wait forever)
wait