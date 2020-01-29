#!/usr/bin/env bash

cur_dir=`pwd`

# Get the script_dir in a way that works for both linux and mac
# readlink behaves differently on linux and mac. The following work work on mac:
#   script_dir=`dirname $(readlink -f $0)`
# https://stackoverflow.com/questions/1055671/how-can-i-get-the-behavior-of-gnus-readlink-f-on-a-mac
SOURCE=$0
while [ -h "$SOURCE" ]; do
  TARGET="$(readlink "$SOURCE")"
  if [[ $SOURCE == /* ]]; then
    SOURCE="$TARGET"
  else
    script_dir="$( dirname "$SOURCE" )"
    SOURCE="$script_dir/$TARGET"
  fi
done
script_dir="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

cd $HOME/.dynamodb_local

PID_FILE="dynamo.pid"

if [ -s "$PID_FILE" ]; then
  PID="$(cat $PID_FILE)"

  echo "dynamo db about to be stopped (with process id: $PID)"
  kill "$PID"
  echo "dynamo db stopped (with process id: $PID)\n"
else
  echo "no pid file present (at $PID_FILE)\n"
fi

cd $cur_dir
