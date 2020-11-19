#!/bin/bash

set -o pipefail

kill_complete() {
  process_to_kill=${1}

  tries=3
  until ! pkill -0 -f "${process_to_kill}" > /dev/null 2>&1
  do
    tries=$((tries-1))
    if [ "$tries" -eq "2" ]; then
      echo
      echo "Killing $process_to_kill before restarting it"
      pkill -f "${process_to_kill}" > /dev/null 2>&1
      sleep 1
    elif [ "$tries" -eq "1" ]; then
      pkill -9 -f "${process_to_kill}" > /dev/null 2>&1
      sleep 1
    else
      echo "unable to kill ${process_to_kill}"
      exit 1
    fi
  done
}


GREEN='\033[0;32m'
NC='\033[0m' # No Color
WEBHOOK_PORT=4000
HTTP_SERVER_PORT=8000

JAVA_SAMPLE='/samples/sdk/java-example-app'
PYTHON_SAMPLE='/samples/sdk/python-example-app'
NODE_SAMPLE='/samples/sdk/nodejs-example-app'
DN_SAMPLE='/samples/sdk/dotnet-example-app-tmp'

#kill ngrok process if the exist
kill_complete 'ngrok'

# Creating public Ngrok endpoint for webhook URL
echo -n Starting ngrok..
ngrok http $WEBHOOK_PORT >> /dev/null &

until curl -m 1 -q http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r -e '.tunnels[0].public_url' > /dev/null 2>&1
do
  echo -n "."
  sleep 1
done
NGROK_HOST="$(curl -m 1 -s http://127.0.0.1:4040/api/tunnels 2> /dev/null | jq -M -r '.tunnels[0].public_url' | cut -d'/' -f3)"

export WEBHOOK_URL="https://${NGROK_HOST}"

# Creating public Ngrok endpoint for static HTTP server (serves QR code page)
ngrok http $HTTP_SERVER_PORT >> /dev/null &

until curl -m 1 -q http://127.0.0.1:4041/api/tunnels 2> /dev/null | jq -M -r -e '.tunnels[0].public_url' > /dev/null 2>&1
do
  echo -n "."
  sleep 1
done
NGROK_HOST="$(curl -m 1 -s http://127.0.0.1:4041/api/tunnels 2> /dev/null | jq -M -r '.tunnels[0].public_url' | cut -d'/' -f3)"

export HTTP_SERVER_URL="https://${NGROK_HOST}"

# Create html pages to serve QR code
echo '<div style="text-align:center;"><p>Scan this QR code with your ConnectMe app</p><br><br><img src="qrcode.png" height=300px width=300px alt="qrcode goes here"></img></div>' \
 | tee \
   $NODE_SAMPLE/qrcode.html \
   $PYTHON_SAMPLE/qrcode.html \
   $JAVA_SAMPLE/qrcode.html \
   $DN_SAMPLE/qrcode.html \
 >/dev/null

#kill static HTTP server if exists
kill_complete "http-server"

# Start static HTTP server
http-server -p $HTTP_SERVER_PORT -c-1 &>/dev/null &

echo
printf "Webhook URL set up at: ${GREEN}${WEBHOOK_URL}${NC}\n"
printf "Static HTTP server (serves QR code page) set up at: ${GREEN}${HTTP_SERVER_URL}${NC}\n"
printf "Using Verity Application Endpoint: ${GREEN}${VERITY_SERVER}${NC}\n"
echo

/bin/bash