# Getting Started with the Verity SDK for Python

The Python example application is a simple showcase of the Python Verity SDK.

Before performing this procedure go to: [Standing Up an Evaluation Verity Application](../VerityInstance.md)

**Prerequisites**

Install the following items:
* `libindy` - Follow the instructions on the [indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` - Follow the instructions on the [Ngrok website](https://ngrok.com/download).
* `Python3` - Follow the instructions on the [Python3 website](https://www.python.org/downloads/)

### Clone the Verity SDK Repo

```sh
git clone git@gitlab.corp.evernym.com:dev/verity/verity-sdk.git
```

**Repo Requirements**
<!--What do they need to do about this? Get access? Download it?-->
* Public PyPi repo

## Build the Python Verity SDK
1. Go to the Python Verity SDK project folder
  
   ```sh
   cd verity-sdk/sdk/python-sdk
   ```

2. Install dependencies

   ```sh
   pip install -r requirements.txt
   ```
   
## Run the Application

1. **Start `Ngrok`**

   `Ngrok` allows the Verity application to reach a webhook endpoint that the example application starts. By default, the example application uses port `4000`, so `Ngrok` must be started to proxy to that local port:
   
   ```sh
   ngrok http 4000
   ```
   
   `Ngrok` should be running at the same time as the example application, and the endpoint should be given to the example during the *Setup* interaction.
   
1. **Set up Connect.Me**

   The interaction that is simulated in the example application requires the Connect.Me mobile app. 

   * Find Connect.Me in the Apple and Android app stores. 
   * Follow the [Connect.Me Setup Instructions](../ConnectMe.md)

   
1. **Launch the example application**
   
   Start the example application and begin the simulated interaction.
   
   ```sh
   python3 example.py
   ```

Continue to [Interactions](/..Interactions.md).

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.