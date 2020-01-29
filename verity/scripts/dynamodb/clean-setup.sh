#!/usr/bin/env bash

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

bash "$script_dir"/stop.sh
bash "$script_dir"/clean-db-data.sh
bash "$script_dir"/start.sh
bash "$script_dir"/create-tables.sh

cd $cur_dir
