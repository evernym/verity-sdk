# Verity-sdk

A proof of concept of the sdk portion of verity.

## Overview

The purpose of verity-sdk is to allow a developer consuming verity application to easily plug into verity without needing to worry about
encryption, key management (unless they want to), and the necessary communication protocols to issue commands to verity appliation.

## Setup

This project is broken up into two major portions: the sdk, and the server. The server is a MOCK verity-2.0 backend to simply test and build out the 
verity-sdk. It is not intended to replace the verity appication currently under development. 

In order to set up the application cd into both the server and the sdk and follow the readme for set up in both directories.

# Tasks
* Figure out how logging will work, including indy logs
* Is there a way to package libindy.so inside the .jar?
