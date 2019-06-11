#!/bin/bash
set -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
EXIT_CODE=0

cd $SCRIPT_DIR/../example
# Run the example app in background
./run.sh
my_pid=$!
sleep 120 # Wait for invitation details to be written to file
cd $SCRIPT_DIR/../../../tools/

# POST invite details to autoresponder
if [ -z "$1" ]; then
  AUTORESPONDER_HOST="localhost"
else
  AUTORESPONDER_HOST=$1
fi
echo "invite details: "
cat $SCRIPT_DIR/../example/inviteDetails.json
curl -X POST --data-binary "$SCRIPT_DIR/../example/inviteDetails.json" http://$AUTORESPONDER_HOST:4002/connect

echo "Waiting for ./run.sh to exit"
wait $my_pid
exit $?
