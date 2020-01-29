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
cd $script_dir

# create tables
sleep_time=5
echo -e "all required tables about to be created (in $sleep_time seconds)"
sleep "$sleep_time"

table_def_file_names=( cas_journal_table_definition.txt cas_snapshot_table_definition.txt eas_journal_table_definition.txt eas_snapshot_table_definition.txt verity_application_journal_table_definition.txt verity_application_snapshot_table_definition.txt )
for item in ${table_def_file_names[*]}
do
  echo -e "\n"
  curl 'http://localhost:8000/' -H 'X-Amz-Target: DynamoDB_20120810.CreateTable' -H 'Authorization: AWS4-HMAC-SHA256 Credential=cUniqueSessionID/20171230/us-west-2/dynamodb/aws4_request, SignedHeaders=host;x-amz-content-sha256;x-amz-date;x-amz-target;x-amz-user-agent, Signature=42e8556bbc7adc659af8255c66ace0641d9bce213fb788f419c07833169e2af8' --data-binary "@$item"
done

echo -e "\n\nall required tables created\n"
