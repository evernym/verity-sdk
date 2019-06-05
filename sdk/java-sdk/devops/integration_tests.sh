#!/bin/bash
set -e
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
EXIT_CODE=0

cd $SCRIPT_DIR/example
# Run the example app in background
./run.sh &
my_pid=$!
sleep 15 # Wait for invitation details to be written to file
cd $SCRIPT_DIR/../../tools/
# Run client auto responder
curl -X POST --data-binary "$SCRIPT_DIR/example/inviteDetails.json" http://localhost:4002/connect

# FIXME: From here, exit with same status as ./run.sh

while   ps | grep " $my_pid "     # might also need  | grep -v grep  here
do
      echo $my_pid is still in the ps output. Must still be running.
          sleep 3
        done

        echo Oh, it looks like the process is done.
        wait $my_pid
        # The variable $? always holds the exit code of the last command to finish.
        # Here it holds the exit code of $my_pid, since wait exits with that code. 
        my_status=$?
        echo The exit status of the process was $my_status


