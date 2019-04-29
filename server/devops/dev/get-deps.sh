#!/bin/bash
SCRIPT=$0
PACKAGE_NAME=$1

DEPS=("libvcx" "libindy" "libsovtoken" "libnullpay")

if [ -z "$PACKAGE_NAME" ]; then
  echo "Missing parameter. Usage: get-deps.sh package_name=version"
  exit 1
fi

function q {
  if [ $? -ne 0 ]; then
    echo "${1}"
    exit 1
  fi
}

INFO=$(apt-cache show $PACKAGE_NAME)
q "apt-cache show ${PACKAGE_NAME} failed"

DEPS_FOUND=""
for DEP in "${DEPS[@]}"
do
  DEP_VERSION=$(echo "${INFO}" | grep "${DEP} (=" | sed -r "s/.*${DEP} \(=\s*(([a-zA-Z\\.=\~0-9+-])+)\).*/${DEP}=\\1/")
  if [ -z "${DEP_VERSION}" ]; then
    continue
  fi
  DEPS_FOUND="$(${SCRIPT} ${DEP_VERSION}) ${DEPS_FOUND}"
done
echo "${PACKAGE_NAME} ${DEPS_FOUND}"


