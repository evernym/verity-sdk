#!/bin/bash

set -e

if [ "$#" -eq 1 ]; then
  ENTERPRISE_SEED=$1
  AGENCY_URL='https://eas01.pps.evernym.com'
  GENESIS_PATH='eas01.txn'
elif [ "$#" -eq 2 ]; then
  ENTERPRISE_SEED=$1
  AGENCY_URL=$2
  GENESIS_PATH='team1.txn'
else
  echo "Usage: ./verity-entrypoint.sh ENTERPRISE_SEED [AGENCY_URL]"
  exit 1
fi

/usr/share/libvcx/provision_agent_keys.py $AGENCY_URL thiskeyisforthewallet1234 --enterprise-seed $ENTERPRISE_SEED > /etc/verity-server/vcxconfig.json
sed -i "s/\"genesis_path\": \"<CHANGE_ME>\"/\"genesis_path\": \"\/var\/lib\/verity-server\/$GENESIS_PATH\"/g" /etc/verity-server/vcxconfig.json
sed -i 's/"institution_name": "<CHANGE_ME>"/"institution_name": "test"/g' /etc/verity-server/vcxconfig.json
sed -i 's/"institution_logo_url": "<CHANGE_ME>"/"institution_logo_url": "https:\/\/robohash.org\/verity-server"/g' /etc/verity-server/vcxconfig.json
sed -i '9 a "payment_method": "null",' /etc/verity-server/vcxconfig.json
echo "vcxconfig.json written to /etc/verity-server/vcxconfig.json"

nodemon ./build/src/app.js
