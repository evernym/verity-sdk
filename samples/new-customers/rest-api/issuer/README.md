## Sample Verity Issuer App

This is a sample Issuer app which is using Verity REST API. This sample app is adapted to the [new changes](https://gitlab.com/evernym/verity/verity-sdk/-/tree/main/docs/new-customers) on the Sovrin Staging Net.

Requirements:
- You have received Verity application endpoint, Domain DID and REST API key from Evernym
- You have a Credential Definition endorsed on the Staging Net (see manual for new customers [here](https://gitlab.com/evernym/verity/verity-sdk/-/tree/main/docs/new-customers))
- You have NodeJs v12 installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/))

> **NOTE**: The issuer application webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application server can send messages to it. **Ngrok** is used here as a developer tool to provide a publicly available endpoint that tunnels to the local listening port of the Issuer App. If you have capabilities to start the Issuer application on a cloud infrastructure then you don't need to install and start ngrok - you just need to specify your URL address in the **webhookUrl** parameter (e.g. `http://<your_cloud_ip>:4000/webhook`)

To try out sample Issuer app follow these steps:
- In a separate terminal window start ngrok for port 4000 and leave it running :
```sh
ngrok http 4000
```
- Install required NodeJs packages:
```sh
npm install
```
- Change the values for **verityUrl**, **domainDid**, **xApiKey**, **webhookUrl**, **credDefId** and **credentialData** with your values in the **Issuer.js** file:
```javascript
const verityUrl = '<< PUT VERITY APPLICATION SERVER URL HERE >>' // address of Verity Application Server
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Server
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint
const credDefId = '<< PUT YOUR CREDENTIAL DEFINITION ID HERE >>'
const credentialData = '<< PUT CREDENTIAL DATA HERE >>' // Data you'll issue in a credential.
```
Sample values might look like this:
```javascript
const verityUrl = 'https://vas.pps.evernym.com'
const domainDid = 'W1TWvjCTGzHGEgbzdh5U4b'
const xApiKey = 'AkdrCwUhNXiQi3zgwKw2KhR6muAX1Q18phP4cfuMtvq4:4cBQC9EsbMa9T96KA4noZwLJQuVcd6KBwaqFhRqZQKFWT45VEm3jbPCm8S6bqhwh3UKEKAPkHeLz9Gb1d1YE1dWv'
const webhookUrl = 'https://1326d835655f.ngrok.io/webhook'
const credDefId = '282aNwH4oHuqq3rg911TVB:3:CL:224867:latest'
const credentialData = {
    name: 'Joe Smith', 
    degree: 'Bachelors'
    }
```
- Start Issuer app
```sh
node Issuer.js
```
Observe messages being exchanged between an Issuer app and a Verity application server on the console output. Scan QR code with a ConnectMe device to establish the connection when required.
