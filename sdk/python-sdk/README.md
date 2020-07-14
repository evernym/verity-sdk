# Verity SDK

This is the official Python SDK for Evernym's Verity application. 

This procedure shows you how to set up your Python development environment to use the Verity SDK. 

## Prerequisites
Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` -- This is a temporary installation to facilitate early experimentation. 
Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `Python3` -- Follow the instructions on the [Python3 website](https://www.python.org/downloads/)

> **NOTE:** Verify that you have read access to the Public PyPi repo.

## Build the Python Verity SDK
Go to the directory where you extracted the tarball.

1. Go to the Python Verity SDK project folder
  
   ```sh
   cd verity-sdk/sdk/python-sdk
   ```

2. Install dependencies

   ```sh
   pip install -r requirements.txt
   ```
3. Build and install `verity-sdk` to local packages 

    ```sh
    python setup.py develop 
    ```
