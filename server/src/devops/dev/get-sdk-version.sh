#!/bin/bash
if [ -d "server" ]; then
  pushd server > /dev/null
fi
VERSION=$(grep "/node-vcx-wrapper_" package.json | sed -E "s/.*node-vcx-wrapper_([0-9\.a-z-]+)_amd.*/\\1/")
DIR_STACK=$(dirs)
if [ "${#DIR_STACK[@]}" -gt "1" ]; then
  popd > /dev/null
fi
echo ${VERSION}