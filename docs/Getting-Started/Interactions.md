# Interactions

### Previous Step

Language-specific setup:
* [Java](/docs/Getting-Started/java/README.md)
* [NodeJs](/docs/Getting-Started/nodejs/README.md)
* [Python](/docs/Getting-Started/python/README.md)
---

After you launch the example application in the previous step, the example application completes a series of interactions:

1. **Setup**
   
   During this setup interaction, three tasks are accomplished:
   
   1. Provisioning an Agent:
   
      This provisioning creates an Agent that serves one **Identity Owner** During that process keys are generated and exchanged. 

   > **NOTE:** When you run the provisioning script during your own tests, make sure you do not run the script more than one time; otherwise, you will get an error and will need to contact Evernym for next steps.

   2. Registering a webhook:
   
       The webhook is a URL for a web server that will receive communication from the Verity server instance. It must be publicly available. (`ngrok` is generally used to meet that requirement.)

   3. Setting up an Issuer identity:
   
      This is a public identity that will need to be written to a Sovrin Ledger (for this demo use-cases the Sovrin Staging should be used). This identity will be used and associated with Credentials issued by this Agent previously provisioned.
   
   When setting up for the first time, the example application will require these inputs from the user:

   * The URL for the Verity server instance that it will be connecting to
   
   * The `Ngrok` URL for the webhook that you started earlier
   
   Additionally, the issuer identity must be written to the ledger, using the
    [Sovrin Self-Serve Website](https://selfserve.sovrin.org/) for the Sovrin StagingNet. 
    The DID and Verkey will be displayed in the console, and they must be transferred accurately 
    to the self-serve site. The example application will pause and wait for this state to be 
    completed. Press **Enter** as soon as the identity is on the ledger.
   
   After initial setup, the context for the Verity SDK is saved to disk and can be reused. 

1. **Connect**

   Connecting is used to form a pairwise relationship between the Identity owner represented by the Agent provision, and the user of Connect.Me. This connection interaction is between two parties and happens over aysncronous messages between these parties. 
   
   Connecting is largely automated in the example application. You can view incoming messages 
   in the console as they arrive from the Verity server instance.
    
   A QR code image will be generated and placed in the current working directory. Scan this QR code with Connect.Me to finish forming the connection between the Verity server instance and Connect.Me.

2. **Write Schema to Ledger**
   
   The `schema` is an essential element of Verifiable Credentials Exchange. This `schema` must be publicly available from a trusted source. The Identity ledger provided by Sovrin is used for the purpose. The `schema` is used to express the shape of the data in the credential. This interaction uses the `write-schema` protocol to both create and write the `schema` object to the ledger.

   Writing the schema to the ledger is fully automated in the example application. You can view 
   incoming messages in the console as they arrive from the Verity server instance.

3. **Write Credential Definition to Ledger**

   The `credDef` object is similar to the `schema` object. It is also used for Verifiable Credentials Exchange and writen to an identity ledger. The `credDef` is used to hold public keys for an Issuer. This interaction uses the `write-cred-def` protocol to both create and write the `credDef` object to the ledger.

   Writing the credential definition to the ledger is fully automated in the example application. 
   You can view incoming messages in the console as they arrive from the Verity server instance.

3. **Issue Degree Credential**
   
   The `issue-credential` protocol used in this interaction is the heart of the Verifiable Credentials Exchange. Using the `schema`, `credDef` and `connection` created before, a Credential can issued. 
   
   Issuing a credential is automated in the example application. It will wait for steps to be 
   completed in Connect.Me asynchronously. Accept the credential in Connect.Me when it pops up.

4. **Request Proof Presentation**
   The `present-proof` protocol used in this interaction allow the final step of the Verifiable Credentials Exchange. Requesting a presentation of proof derived by a verifiable credential allow for the exchange of information from parities that test each other. 

   The request for proof is automated in the example application. It will wait for steps to be 
   completed in Connect.Me asynchronously. Accept the proof in Connect.Me when it pops up.
   
After the proof is exchanged with the Verity server instance, the simulated interaction is complete, and the example application terminates.

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.