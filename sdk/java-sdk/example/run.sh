#!/bin/bash
cd ..
SDK_DIR=$(pwd)
cd example

java -Dfile.encoding=UTF-8 -cp "$SDK_DIR/target/java-sdk-0.22-SNAPSHOT.jar:$(mvn -q exec:exec -Dexec.executable=echo -Dexec.args='%classpath')" com.evernym.sdk.example.App
