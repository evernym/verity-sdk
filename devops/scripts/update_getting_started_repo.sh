#!/bin/bash
set -e

if [ ! -z "$GETTING_STARTED_DEPLOY_KEY" ]; then
  eval `ssh-agent -s`
  echo $GETTING_STARTED_DEPLOY_KEY | ssh-add - > /dev/null
fi

TEMP_DIR=/tmp/Getting-Started-With-The-Verity-SDK

rm -rf $TEMP_DIR
mkdir $TEMP_DIR
cp docs/Getting-Started-With-The-Verity-SDK/* $TEMP_DIR/.
cp sdk/java-sdk/example/src/main/java/com/evernym/sdk/example/App.java $TEMP_DIR/.
cp sdk/java-sdk/example/src/main/java/com/evernym/sdk/example/Listener.java $TEMP_DIR/.
sed -i 's/package com.evernym.sdk.example;/package com.mycompany.app/g' $TEMP_DIR/App.java
sed -i 's/package com.evernym.sdk.example;/package com.mycompany.app/g' $TEMP_DIR/Listener.java

cp tools/new_did.py $TEMP_DIR/.
cp tools/provision_sdk.py $TEMP_DIR/.
cat tools/requirements.txt | grep requests >> $TEMP_DIR/python-dependencies.txt
cat tools/requirements.txt | grep python3-indy >> $TEMP_DIR/python-dependencies.txt

cd $TEMP_DIR
git init
git add -A
git commit -m "forced update of getting started guide"
git remote add origin git@github.com:evernym/Getting-Started-With-The-Verity-SDK.git
git push -u origin master -f
