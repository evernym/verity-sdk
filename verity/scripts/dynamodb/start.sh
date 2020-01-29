#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "$0" )" && pwd )"

sh $SCRIPT_DIR/stop.sh

cd "$HOME"/.dynamodb_local
PID_FILE="dynamo.pid"
# Remove the dynamo.pid file iff it exists
[ -e "${PID_FILE}" ] && rm -f "$PID_FILE"

echo "dynamo db about to be started"
nohup java -Djava.library.path=./dynamodb_local -jar ./DynamoDBLocal.jar -sharedDb -dbPath $HOME/.dynamodb_local > "dynamodb-output.log" 2>&1 &
echo $! > "$PID_FILE"

echo -e "dynamo db started (with pid: $(cat $PID_FILE))\n"

sleep 5
echo -e "dynamo db log file content"
cat dynamodb-output.log
