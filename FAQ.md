### Verity SDK vs REST API
There are two general approaches to working with Verity. There is a language agnostic REST API and native SDKs in four popular languages. Learning a little about each can help you select the right approach for your current use case and long term plans.The Verity REST API makes it easy to get started from whatever environment you have already adopted. The OpenAPI interactive documentation, sample applications, issuer tutorial, and verifier tutorial can help you get started. The Verity REST API uses standard HTTPS for security and has no installation dependencies, which makes it appropriate for serverless environments. When using the REST API, you should pay special attention to how it uses webhooks for asynchronous communication.
	
If you find it more convenient to use an API focused on your programming language of choice, then the native SDK is right for you. We offer native SDKs for Java, Node JavaScript, Python, and .NET. Each SDK includes a getting started guide and example applications.These SDKs provide helper methods and functions that assist in constructing requests. The SDKs also provide DIDComm-based message level encryption and security when communicating with Verity. Note that the installation of the SDK requires some management, such as backing up the local SQLite wallet that stores the encryption key for DIDComm communication. Over time we plan to enhance the native SDKs to provide additional features that don't fit within a REST approach.

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


