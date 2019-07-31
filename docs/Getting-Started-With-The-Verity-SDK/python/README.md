
# Getting Started with the Verity SDK for Python

## Running the Example Python Application (Ubuntu 16.04)

These steps target the x86-64 architecture running Ubuntu 16.04. If you want to run this inside a fresh VM, install [vagrant](https://www.vagrantup.com/) and in an empty folder run:

```sh
vagrant init ubuntu/xenial64
vagrant up
vagrant ssh
```

1. Start out by cloning this repository

    ```
    git clone https://github.com/evernym/Getting-Started-With-The-Verity-SDK
    cd Getting-Started-With-The-Verity-SDK
    ```

2. You will need docker, libindy and python3.6 installed on your system

    ```sh
    sudo apt-get update
    sudo apt-get install -y docker.io
    sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88
    sudo add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master"
    sudo apt-get update
    sudo apt-get install -y libindy=1.9.0~1122
    sudo add-apt-repository ppa:jonathonf/python-3.6
    sudo apt-get update && sudo apt-get install -y python3.6 python3-pip
    ```
	
3. You will need to be a Trust Anchor on the Sovrin Test Ledger to run this example. Use the `tools/new_did.py` script to create a new DID and then email the DID and VerKey to [support@sovrin.org](mailto:support@sovrin.org) asking for that DID to be written to the Staging Net. You should received a response within 12 hours on a weekday (often much sooner!).

	```
	pip3 install -r tools/requirements.txt --user
	./tools/new_did.py
	```

	:exclamation: Don't send the seed! Keep it safe. You will need it to start the Mock Verity application and any other time you want to write to the Test Ledger.


4. Start the Mock Verity service locally with your Trust Anchor Seed created in step 2.

	```sh
	sudo docker pull evernymdev/mock-verity # Get latest version
	sudo docker run -itd --rm --network host evernymdev/mock-verity <YOUR_TRUST_ANCHOR_SEED>
	```

1. Provision against Mock Verity

	This is the process whereby your application registers and exchanges keys with the Verity application. You will also need `provision_sdk.py` from this repository. Make sure to replace \<WALLET\_KEY\_HERE\> with a new wallet password.

    ```sh
    mkdir python/example
    python3 tools/provision_sdk.py --wallet-name verity-sdk-test-wallet http://localhost:8080 <WALLET_KEY_HERE> > python/example/verityConfig.json
    ```
	
	This wallet is only used to store the keys you use to communicate with Verity.
	
	:exclamation: If you restart Mock Verity at any point, you will need to provision again.


2. Run the example application.

    ```sh
    cd python/
    python3.6 -m pip install -r requirements.txt --user
    python3.6 example.py
    ```
	
2. The logs for the example application will print something like this:
	
	```
	Invite Details: {"sc":"MS-101","threadId":null,"s"...
	```
	
	Copy the invite details JSON object into any QR code generator.  [This one](https://www.qr-code-generator.com/) works well, but you will need to click on the "Text" tab. 

1. Download [Connect.Me](https://connect.me/) from the [App Store](https://itunes.apple.com/us/app/connect-me/id1260651672?mt=8) or the [Google Play Store](https://play.google.com/store/apps/details?id=me.connect&hl=en).

1. When you setup the app, make sure to check the "Use Staging Net" option at the bottom of the screen or this demo won't work.

	![Connect.Me Developer Mode Switch](https://i.postimg.cc/pTrdMszg/IMG-0116.png)

1. Scan the QR code with Connect.Me. You will then receive a Connection Invite, Challenge Question, Credential Offer, Credential and Proof Request in that order, demonstrating all of the protocols currently supported by the Verity SDK.

© 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.
