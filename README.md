# Verity-sdk Repo

## Overview

The goal of the VeritySDK is easy application integration with the Verity server. This integration allows for SSI-enabled
workflows like creating connections, issuing credentials, requesting proof, and general pairwise interactions. The SDK
uses libindy and Agent-to-Agent protocols to securely authenticate and communicate with the Verity server. The creation of
language specific bindings allows for developers to quickly integrate their backend services into the world of SSI with
minimal overhead.

## Setup

The general steps for setting up the VeritySDK for development:

1) Install libindy and its python wrapper (https://github.com/hyperledger/indy-sdk)
2) As the User/UID that will use the SDK execute the tools/provisioner/provision_sdk.py script and save the resulting 
config file. 
3) Download/integrate the language specific library of your choosing.
4) Write and test your application making sure to use the config file from step 2 during initialization and also making
sure to use the same User/UID from step 2.

## Repo structure

This repo is broken up into two major portions: the sdk, and the server. The SDK contains the language specific bindings
for interacting with the Verity server in an idiomatic way. The server is a MOCK Verity server to simplify integration and 
testing of applications. It runs an in-memory only service that can be deployed easily for application testing but lacks
many key features needed for production use.

Take time to become familiar with the language specific SDK of your choice starting with its README. 
