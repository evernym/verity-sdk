## Sample Verity Verifier App

This is a sample Verifier app which is using Verity REST API.
For more information on this Sample Verifier app read this explanatory [document](../../../docs/howto/How-to-build-Verifier-using-REST-API.md)

Requirements:
- You have received Verity application endpoint, Domain DID and REST API key from Evernym
- You have NodeJs v12 installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/))

> **NOTE**: The Verifier application webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application server can send messages to it. **Ngrok** is used here as a developer tool to provide a publicly available endpoint that tunnels to the local listening port of the Verifier App. If you have capabilities to start the Verifier application on a cloud infrastructure then you don't need to install and start ngrok - you just need to specify your URL address in the **webhookUrl** parameter (e.g. `http://<your_cloud_ip>:4000/webhook`)

To try out sample Verifier app follow these steps:
- In a separate terminal window start ngrok for port 4000 and leave it running :
```sh
ngrok http 4000
```
- Install required NodeJs packages:
```sh
npm install
```
- Change the values for **verityUrl**, **domainDid**, **xApiKey**, and **webhookUrl** with your values in the **Verifier.js** file:
```javascript
const verityUrl = '<< PUT VERITY APPLICATION SERVER URL HERE >>' // address of Verity Application Server
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Server
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint
```
Sample values might look like this:
```javascript
const verityUrl = 'https://vas.pps.evernym.com'
const domainDid = 'W1TWvjCTGzHGEgbzdh5U4b'
const xApiKey = 'AkdrCwUhNXiQi3zgwKw2KhR6muAX1Q18phP4cfuMtvq4:4cBQC9EsbMa9T96KA4noZwLJQuVcd6KBwaqFhRqZQKFWT45VEm3jbPCm8S6bqhwh3UKEKAPkHeLz9Gb1d1YE1dWv'
const webhookUrl = 'https://1326d835655f.ngrok.io/webhook'
```
- Start Verifier app
```sh
node Verifier.js
```
Observe messages being exchanged between an Verifier app and a Verity application server on the console output. Scan QR code with a ConnectMe device to establish the connection when required.
