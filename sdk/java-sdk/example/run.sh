#!/bin/bash
mvn install:install-file -Dfile=../target/java-sdk-0.1-SNAPSHOT.jar
mvn package
mvn exec:java -D exec.mainClass="com.evernym.sdk.example.App"
#java -cp ../target/java-sdk-0.1-SNAPSHOT.jar:target/example-1.0-SNAPSHOT.jar com.evernym.sdk.example.App