#!/bin/bash

set -e
#JAR_VERSION=$(xml2 < sdk/java-sdk/pom.xml | grep /project/version | cut -d "=" -f 2 | cut -d "-" -f 1)
#echo "Uploading .jar with version number ==> ${JAR_VERSION}"
SETTINGS_PATH=../../devops/maven/settings.xml

cd sdk/java-sdk
sed -i 's/-SNAPSHOT<\/version>/<\/version>/g' pom.xml

if [[ $IO_CLOUDREPO_ACCOUNT_PASSWORD == *[!\ ]* ]] ; then
  sed -i "s/repository-user-password/$IO_CLOUDREPO_ACCOUNT_PASSWORD/g" $SETTINGS_PATH
  mvn --errors deploy --settings $SETTINGS_PATH
else
  echo "Environment variable IO_CLOUDREPO_ACCOUNT_PASSWORD is not defined"
  exit 1
fi
