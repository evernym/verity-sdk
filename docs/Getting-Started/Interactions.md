# Interactions

### Previous Step

Language-specific setup:
* [Java](/docs/Getting-Started/java/README.md)
* [NodeJs](/docs/Getting-Started/nodejs/README.md)
* [Python](/docs/Getting-Started/python/README.md)

After you have finished launching the integration code example in the previous step, the process completes a series of interactions:

1. **Setup**
   
   During setup, three tasks are being accomplished:
   
   1. Provisioning an Agent

   1. Registering a webhook

   1. Setting up an Issuer identity 
   
   When setting up for the first time, the integration code example will require these inputs from the user:
   * The URL for the customer integration code that it will be connecting to
   
   * The `Ngrok` URL for the webhook that you started earlier
   
   Additionally, the issuer identity must be written to the ledger, using the [Sovrin Self-Serve website](https://selfserve.sovrin.org/) for the Sovrin StagingNet. The DID and Verkey will be displayed in the console, and they must be transferred accurately to the self-serve site. The integration code example will pause and will wait <!--Is this correct? It will wait?-->for this state to be completed. Press **Enter** as soon as the identity is on the ledger.
   
   After initial setup, the context for the Verity SDK is saved to disk and can be reused. 

1. **Connect**
   
   Connecting is largely automated in the integration code example. You can view incoming messages in the console as they arrive from the customer integration code.
    
   A QR code image will be generated and placed in the current working directory. Scan this QR code with Connect.Me to finish forming the connection between the customer integration code and Connect.Me.<!--1. Ask a Committed Answer-->

1. **Write Schema to Ledger**

   Writing the schema to the ledger is fully automated in the integration code example. You can view incoming messages in the console as they arrive from the customer integration code.

1. **Write Credential Definition to Ledger**

   Writing the credential definition to the ledger is fully automated in the integration code example. You can view incoming messages in the console as they arrive from the customer integration code.

1. **Issue Degree Credential**
   
   Issuing a credential is automated in the integration code example. It will wait for steps to be completed in Connect.Me asynchronously. Accept the credential in Connect.Me when it pops up.

1. **Request Proof Presentation**

   The request for proof is automated in the integration code example. It will wait for steps to be completed in Connect.Me asynchronously. Accept the proof in Connect.Me when it pops up.
   
After the proof is exchanged with the customer integration code, the simulated interaction is complete and the integration code example will terminate.

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.