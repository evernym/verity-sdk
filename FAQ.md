### Verity SDK vs REST API
There are two general approaches to working with Verity. There is a language agnostic REST API and native SDKs in four popular languages. Learning a little about each can help you select the right approach for your current use case and long term plans. The Verity REST API makes it easy to get started from whatever environment you have already adopted. The [OpenAPI interactive documentation](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#/), [sample applications](samples/rest-api), [issuer tutorial](docs/howto/How-to-build-Issuer-using-REST-API.md), and [verifier tutorial](docs/howto/How-to-build-Verifier-using-REST-API.md) can help you get started. The Verity REST API uses standard HTTPS for security and has no installation dependencies, which makes it appropriate for serverless environments. When using the REST API, you should pay special attention to how it uses webhooks for asynchronous communication.
	
If you find it more convenient to use an API focused on your programming language of choice, then the native SDK is right for you. [We offer native SDKs](sdk) for Java, Node JavaScript, Python, and .NET. Each SDK includes a [getting started guide](docs/getting-started) and [example applications](samples/sdk). These SDKs provide helper methods and functions that assist in constructing requests. The SDKs also provide DIDComm-based message level encryption and security when communicating with Verity. Note that the installation of the SDK requires some management, such as backing up the local SQLite wallet that stores the encryption key for DIDComm communication. Over time we plan to enhance the native SDKs to provide additional features that don't fit within a REST approach.

### Why using asynchronous API?
SSI protocols are asynchronous in their nature, the other side of the interaction is usually a human being with their wallet app and not a DB that provides instant response. Human beings may respond to CredOffer message immediately, or in 5 mins, or in 5 hours. Many of the SSI use cases require end user to be remote and may not be instantly responding to notifications received on their devices. For this reason, we have decideed to design full async API that would cater for wide range of use cases.

### What is an agent?
Agent is a construct in SSI which is defined by piece of code or software component that represents an identity owner in the SSI domain. Agent can be a mobile application on the device in case of individuals or it can be a software running on a server or some web app in case of  companies and organizations. Agent has a permanent relationship to exactly one identity owner, but also holds keys that are authorized to do work for that owner and  speaks and/or listens to agent language (DIDcomm). Usually agents have wallet but is not strictly required.

Types of agents:
* Edge agent: Under direct control of owner. Hence, trust can be high.
* Cloud agent: Hosted in a way that owner has to trust someone else. Hence, trust is lower.

### What is the purpose of CAS - Consumer Agency Service in Evernym's architecture?
Edge agents are usually built as mobile applications on end user's devices. Since end users are on the move and are switching networks often from mobile data to home WiFi and work WiFi, the IP address is changing as well. The nature of DIDComm communications is that it relies on keys and endpoints that are exchanged between the two parties when the DID connection has been established. Since edge agents are changing IP addresses constantly, the Consumer Agency Service does provide this constant endpoint for the edge agents. Therefore all DIDComm messages intended for edge agents are routed through CAS and CAS has a way to reach the edge agent, usually by sending push notifications so those DIDComm messages can be delivered. What is also important to note is that CAS doesn't have a way to "see" the content of messages. It only knows the minimal information needed in order to route and deliver the message to the right edge agent.

### Re-running example application
When you run the example application for the first time, you will be asked to provide a provisioning sandbox token. This will provision an Agent on Verity for you and will result in creation of the local libindy wallet for your SDK and the Verity context. That libindy wallet stores the key which is used to encrypt messages from Verity SDK to Verity Application Service.

In order to be able to re-run your application without provisioning you would need to persist both the context and the libindy wallet (by default libindy wallet is stored in the $HOME/.indy_client/wallet folder). Provisioning is usually done only once in the application lifetime.

### What is the difference between DomainDID and IssuerDID/PublicDID?
DomainDID represents one Identity Domain on Verity platform. You can think of DomainDID like userId of some sort where user is an entity, organization or a company using Verity. 

IssuerDID (also referred to as PublicDID) represents a DID used for signing credentials during issuing. One DomainDID can also have an IssuerDID if they went through the issuer-setup process. In case an entity wants just to act as a Verifier, they don't necessariyly need to go through the issuer-setup process and acquire IssuerDID. Our recomendation is that Verifiers should also acquire IssuerDID (in Verifier's case PublicDID is more appropriate to be used), since it is being used during DID connection establishment for recognizing returning users.

### Is it possible to register two or more different endpoints for receiving responses from Verity?
It is possible to register multiple endpoint for the same DomainDID and if you do that, Verity will send the same response to both registered webhooks. In order to register a different webhook, you'll need to give it a different "id" when making this call https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/UpdateEndpoint/updateEndpoint, for example "id":"webhook1" for your ServiceA and "id:webhook2" for your ServiceB. If you use the same "id" you will override the previous endpoint. For example:

`{
  "@id": "e3692334-2447-4184-8c03-314603793bb7",
  "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
  "comMethod": {
    "id": "webhook1",
    "value": "https://endpoint1.net",
    "type": 2,
    "packaging": {
      "pkgType": "plain"
    }
  }
}`

And

`{
  "@id": "e3692334-2447-4184-8c03-314603793bb7",
  "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
  "comMethod": {
    "id": "webhook2",
    "value": "https://endpoint2.net",
    "type": 2,
    "packaging": {
      "pkgType": "plain"
    }
  }
}`

### Is there a retry mechanism on Verity when sending to the registered webhook fails?
When message sending to webhook fails, currently we only retry the failed messages for 5 times (that too in very short span of 4 minute). For example:
- Original attempt failed at 10:00:00 am
- 1st retry after 15 seconds at 10:00:15 am
- 2nd retry after 30 seconds (post 1st retry) at 10:00:45 am
- 3rd retry after 45 seconds (post 2nd retry) at 10:01:30 am
- 4th retry after 60 seconds (post 3rd retry) at 10:02:30 am
- 5th retry after 75 seconds (post 4th retry) at 10:04:00 am

### How best to horizontally scale a service that integrated with Verity using Verity SDK?
There are two options for horizontally scaling a service on client side using the Verity SDK. Libindy can be reconfigured to use MySQL storage, instead of the SQLite. This is not available out of the box and if you are planning to do this, please contact Evernym support to assist you configuring this. 

The other option is to copy the SQLite db ($HOME/.indy_client) and verity-context.json to each of your horizontally scalable machines running client software. 

### Moving to Production

When ready to move to the production environment, please follow the steps below.

##### STEP 1 - Assign a DomainDID and a REST API key or an SDK provisioning token that points to the PROD Verity SaaS

Please contact Evernym support to receive a DomainDID and a REST API key (if using REST API) or an SDK provisioning token (if using Verity SDK).

Alternatively, you can create DomainDID and REST API key yourself if you use SDK for provisioning.

> At this step, please tell Evernym your desired configuration for the data retention policy (default: no data is stored) and the connection invitation URL (default: Connect.Me).

##### STEP 2 – Set up a webhook

Call `UpdateEndpoint` endpoint that sets up the callback webhook: https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/UpdateEndpoint/updateEndpoint

##### STEP 3 – Set up an Issuer

Call `IssuerSetup` endpoint that creates Issuer DID and Verkey keypair: https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/IssuerSetup

##### STEP 4 – Have your Issuer DID and Verkey written to the Sovrin MainNet as Transaction Endorser

There are two options you can choose from when it comes to transaction endorsement on Sovrin MainNet. First, you can contact Sovrin to become a Transaction Endorser yourself. Alternatively, Evernym can help you endorse your transactions. If you prefer the latter option, the costs of endorsement will be passed to you. Please contact Evernym support to discuss commercial terms.

A list of Sovrin Ledger Fees and a link to the Transaction Endorser Application form are available [here](https://sovrin.org/issue-credentials/).

##### Verity Application Service endpoint for PROD

```
https://vas.evernym.com
```

### Deep links in the Connect.Me

Besides QR code scanning, you can use deep links for a connection invitation in the Connect.Me app. 

A deep link is constructed by prepending URL-encoded inviteURL or shortInviteURL with [https://connectme.app.link?t=]() . 

The deep link URL will look like this:

[https://connectme.app.link?t=https%3A%2F%2Fvty.im%2Fk7gw0]() .

### Improving documentation

Can’t find what you are looking for, or have suggestions on what to include in the docs? [Drop us a line](mailto:support@evernym.com), we'd love to receive your ideas.