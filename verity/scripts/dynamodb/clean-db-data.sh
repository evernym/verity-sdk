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

echo "dynamo db data about to be cleaned"
cd "$HOME"/.dynamodb_local
[ -e shared-local-instance.db ] && rm -f shared-local-instance.db
echo -e "dynamo db data cleaned\n"

cd $cur_dir
