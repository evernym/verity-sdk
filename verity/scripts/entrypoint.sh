#!/bin/bash

set -e

# Parse Arguments
if [ "$#" -eq 1 ]; then
    ENTERPRISE_SEED=$1
else
    echo "Usage: ./entrypoint.sh ENTERPRISE_SEED"
    exit 1
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

# Start DynamoDB
/root/scripts/dynamodb/start.sh

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

echo "Verity Bootstrapping complete. Listening on https://$NGROK_HOST"
# Wait for Verity Application to exit (wait forever)
wait