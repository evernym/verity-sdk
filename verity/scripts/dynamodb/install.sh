#!/usr/bin/env bash

apt-get install -y default-jre

apt-get install -y curl

mkdir -p $HOME/.dynamodb_local

cd $HOME/.dynamodb_local

curl -L http://dynamodb-local.s3-website-us-west-2.amazonaws.com/dynamodb_local_latest.tar.gz | tar xz
