#!/bin/bash
set -e

if [ ! -z "$GETTING_STARTED_DEPLOY_KEY" ]; then
  eval `ssh-agent -s`
  ssh-add - <<< "${GETTING_STARTED_DEPLOY_KEY}"
fi

TEMP_DIR=/tmp/Getting-Started-With-The-Verity-SDK

rm -rf $TEMP_DIR
mkdir $TEMP_DIR
cp -r docs/Getting-Started-With-The-Verity-SDK/* $TEMP_DIR/.

# Add basic tools
mkdir $TEMP_DIR/tools
cp tools/new_did.py $TEMP_DIR/tools/.
cp tools/provision_sdk.py $TEMP_DIR/tools/.
cat tools/requirements.txt | grep requests >> $TEMP_DIR/tools/requirements.txt
cat tools/requirements.txt | grep python3-indy >> $TEMP_DIR/tools/requirements.txt

# Update java/
cp sdk/java-sdk/example/src/main/java/com/evernym/sdk/example/App.java $TEMP_DIR/java/.
cp sdk/java-sdk/example/src/main/java/com/evernym/sdk/example/Listener.java $TEMP_DIR/java/.
sed -i 's/package com.evernym.sdk.example;/package com.mycompany.app;/g' $TEMP_DIR/java/App.java
sed -i 's/package com.evernym.sdk.example;/package com.mycompany.app;/g' $TEMP_DIR/java/Listener.java

# Update python/
cp sdk/python-sdk/example.py $TEMP_DIR/python/.
cat sdk/python-sdk/example/requirements.txt >> $TEMP_DIR/python/requirements.txt
echo -e '\n' >> $TEMP_DIR/python/requirements.txt
cat $TEMP_DIR/tools/requirements.txt >> $TEMP_DIR/python/requirements.txt

# Generate and add Flow Diagram
plantuml -o $TEMP_DIR docs/example-flow.puml
if [ -f $TEMP_DIR/example-app-flow ]; then # Old version of plantuml
  mv $TEMP_DIR/example-app-flow $TEMP_DIR/example-app-flow.png
fi

cd $TEMP_DIR
git init
git config user.email "none"
git config user.name "Evernym CICD"
git add -A
git commit -m "forced update of getting started guide"
if [ ! -z "$1" ]; then
  echo "uploading to GitHub"
  git remote add origin $1 # git@github.com:evernym/Getting-Started-With-The-Verity-SDK.git
  GIT_SSH_COMMAND="ssh -o StrictHostKeyChecking=no" git push -u origin master -f
else
  echo "not uploaded"
fi
