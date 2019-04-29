#!/bin/bash

if [ -z "$KRK_USERNAME" ] ; then
    if [ -t 1 -a -z "$GITLAB_CI" ] ; then
        read -p 'Username: ' KRK_USERNAME
    else
        echo "Must define variable: KRK_USERNAME"
        exit 1
    fi
fi
if [ -z "$KRK_PASSWORD" ] ; then
    if [ -t 1 -a -z "$GITLAB_CI" ] ; then
        echo -n 'Password: '
        read -s KRK_PASSWORD
    else
        echo "Must define variable: KRK_PASSWORD"
        exit 1
    fi
fi

curl_config="user = $KRK_USERNAME:$KRK_PASSWORD"
TO_REPO=${KRK_REPO:-internal}
UPLOAD_DEV_BUILDS=${UPLOAD_DEV_BUILDS:-false}

for pkg in "${@}"; do
    if [[ $pkg =~ [0-9]+dev ]] ; then
        if [ "$UPLOAD_DEV_BUILDS" == 'false' ] ; then
            echo "Package: $pkg is a development build, skipping!"
            continue
        fi
    fi
    echo "Uploading: '${pkg}' to: '$TO_REPO'"
    curl -K <(cat <<<"$curl_config") -X POST -F file=@${pkg} https://kraken.corp.evernym.com/repo/${TO_REPO}/upload
    rc=$?
    if [ $rc -ne 0 ] ; then
        echo -e "\nFailed uploading package: '${pkg}' to '$TO_REPO'"
        break
    fi
    echo
done