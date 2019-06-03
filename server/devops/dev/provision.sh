#!/bin/bash

/usr/share/libvcx/provision_agent_keys.py https://eas01.pps.evernym.com thiskeyisforthewallet1234 --enterprise-seed 000000000000000000000000Trustee1 > /etc/verity-server/vcxconfig.json
sed -i 's/"genesis_path": "<CHANGE_ME>"/"genesis_path": "\/var\/lib\/verity-server\/pool.txn"/g' /etc/verity-server/vcxconfig.json
sed -i 's/"institution_name": "<CHANGE_ME>"/"institution_name": "test"/g' /etc/verity-server/vcxconfig.json
sed -i 's/"institution_logo_url": "<CHANGE_ME>"/"institution_logo_url": "https:\/\/robohash.org\/verity-server"/g' /etc/verity-server/vcxconfig.json
sed -i '9 a "payment_method": "null",' /etc/verity-server/vcxconfig.json
echo "vcxconfig.json written to /etc/verity-server/vcxconfig.json"
