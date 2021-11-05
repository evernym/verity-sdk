# Sample app using Out-of-band request-attach

This is a sample application that demonstrates request-attach functionality of the Out-of-Band (OOB) protocol, and it is adapted to the [new changes](https://gitlab.com/evernym/verity/verity-sdk/-/tree/main/docs/new-customers) on the Sovrin Staging Net.

This sample app is using the [Verity REST API](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0).

The app is written for Node.js framework, but it can be easily adapted to any language that supports asynchronous web requests.

## About request-attach feature

The Out-of-Band (OOB) protocol is defined in the [Aries RFC 0434](https://github.com/hyperledger/aries-rfcs/blob/master/features/0434-outofband/README.md)

When issuing credential a credential offer can be attached to the OOB invitation.

When requesting proof a proof request can be attached to the OOB invitation.

This allows for supression of connection establishment interactions required from the Holder and a smoother user experience (obtaining a credential or sharing a proof response by a single QR code scan)

> NOTE: When there is a credential offer or a proof request attached to the invitation, the ConnectMe app will implicitly accept the connection (connection acceptance screen will not be shown in the UI) and will present credential offer or proof request from the OOB invitation attachment.

A new field `by_invitation` has been introduced in the **PresentProof** and **IssueCredential** Verity REST API calls.
The default value for this argument is **false**, which requires that connection has already been established prior to issuing credential or requesting proof.

However, when this argument is set to **true**, then connection with the Inviter is established during execution of **IssueCredential** or **PresentProof** protocol.

After sending a credential offer (or requesting a proof) with the `by_invitation` field set to **true**, Verity Application Service will respond with the `protocol-invitation` signal message, in which `inviteURL` field will contain OOB invitation with credential offer or proof request attached.
Since `inviteURL` with attachments can get very long, the `protocol-invitation` message will also contain the `shortInviteURL` field with a shortened URL which redirects to the `inviteURL` and which is more friendly for encoding into QR code.

> NOTE: Improved UX comes at the expense of privacy. In the case of credential offer, the **inviteURL** will contain base58 encoded values of credential attributes (PII data). In the case of proof request, the **inviteURL** will contain requested attributes names and imposed restrictions (no PII data is present in the `inviteURL` in this case)

## Pre-requisites

This app requires Verity Application instance provided in the cloud by Evernym in order to have end2end functionality.

The application will require you to specify:
* Verity Application Service endpoint
* Domain DID
* X-API-KEY
* Webhook Endpoint
* Credential Definition Id
* Credential Data
* Proof Attributes

You should have received this data from Evernym when you signed up for a free developer account [on our website](https://www.evernym.com/developer/).

> **NOTE**: You can reuse your Domain DID and X-API-KEY for running sample apps and/or building your own. 

The application will also need to expose a webhook endpoint. The webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application service can send messages to it. For development purposes, the application listening port can be assigned a public URL via means of the Ngrok tool.

### How to run the app

Requirements to run this sample app:
- You have received Verity application endpoint, Domain DID and REST API key from Evernym
- You have a Credential Definition endorsed on the Staging Net (see manual for new customers [here](https://gitlab.com/evernym/verity/verity-sdk/-/tree/main/docs/new-customers))
- You have NodeJs v12 installed
- You have Ngrok installed (or you can start this app on a public infrastructure).

> **NOTE**: The application webhook endpoint (**/webhook**) needs to be served on a public URL so that Verity Application service can send messages to it. **Ngrok** is used here as a developer tool to provide a publicly available endpoint that tunnels to the local listening port of the app. If you have capabilities to start the application on a cloud infrastructure then you don't need to install and start ngrok - you just need to specify your URL address in the **webhookUrl** parameter (e.g. `http://<your_cloud_ip>:4000/webhook`)

To try out the app follow these steps:
- In a separate terminal window start ngrok for port 4000 and leave it running :
```sh
ngrok http 4000
```
- Install required NodeJs packages:
```sh
npm install
```
- Change the values for **verityUrl**, **domainDid**, **xApiKey**, **webhookUrl**, **credDefId**, **credentialData** and **proofAttributes** with your values in the **oob-request-attach.js** file:
```javascript
const verityUrl = '<< PUT VERITY APPLICATION SERVICE URL HERE >>' // address of Verity Application Service
const domainDid = '<< PUT DOMAIN DID HERE >>' // your Domain DID on the multi-tenant Verity Application Service
const xApiKey = '<< PUT X-API-KEY HERE >>' // REST API key associated with your Domain DID
const webhookUrl = '<< PUT WEBHOOK URL HERE >>' // public URL for the webhook endpoint
const credentialData = '<< PUT CREDENTIAL DATA HERE >>' // Data you'll issue in a credential
const proofAttributes = '<< PUT AN ARRAY OF ATTRIBUTES >>' // Data you'll request proof of
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
const proofAttributes = [
  {
    name: 'name',
    restrictions: [],
    self_attest_allowed: true
  },
  {
    name: 'degree',
    restrictions: [],
    self_attest_allowed: true
  }
]
```
> **NOTE**: These are just sample reference values. These values will NOT work if left unchanged. You should specify the DOMAIN_DID and the X_API_KEY that you received from Evernym
- Start the app
```sh
node oob-request-attach.js
```
Observe the messages being exchanged between the app and the Verity Application Service on the console output. Scan the QR code with a ConnectMe app to accept the credential offer or to share the proof presentation when requested
