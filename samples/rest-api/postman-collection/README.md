# Verity REST API Postman Collection

This Postman collection contains examples of requests for most endpoints of the [REST API for Verity](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#). 

## Prerequisites
- You received Domain DID and API key from Evernym
- You have [Postman](https://www.postman.com/) installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/))

> **NOTE**: **Ngrok** is used as a developer tool to provide a publicly available endpoint that tunnels to the local listening port. You will need a publicly available endpoint to be able to receive messages from the Verity Application Server. 

## Steps
You can use this collection to call the Verity API's endpoints and receive callbacks from the Verity Application Server. 

### STEP 1 – Start Ngrok and listen to callbacks on port 4000
Verity Application Server will send messages to your endpoint in response to your API calls. You will make API calls on the next step, and you should be listening to these responses. 

#### Start Ngrok
In a separate terminal window start ngrok for port 4000 and leave it running:

```sh
ngrok http 4000
```

#### Listen to callbacks on port 4000
You can use the [Simple Listener app](simple-listener-app) available in this repository.

### STEP 2 – Configure Variables in Postman and Update your Endpoint

#### Set Variables in Postman
In Postman, add your `DomainDID` and `Webhook` as variables, and your API key as `X-API-KEY` in Authorization.  

> **NOTE**: **Webhook** is your ngrok endpoint, e.g. `https://a8cd-64-62-226-249.ngrok.io`  
> **DomainDID** and **X-API-KEY** are the Domain DID and API key you received from Evernym. 

#### Update Endpoint
You will first register a webhook where you will be receiving callbacks from the Verity Server. You can do this by calling the [`UpdateEndpoint` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/UpdateEndpoint/updateEndpoint) that sets up the callback webhook.

Run the `Update Endpoint` request in Postman and check that you received a response on your endpoint: 

```json
{
    "id": "webhook",
    "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED"
}
```

If you don't see the response, please check that you have ngrok running on port 4000 and you are listening to responses as described in STEP 1. 

### STEP 3 – Make API Calls

You're now ready to make other API calls. If you don't have your Issuer DID already on the ledger, please run the `Setup Issuer` request first, and contact us to have it endorsed. 

#### Set up Issuer Identity

Unless you previously set up your Issuer and we have already endorsed it, run the `Setup Issuer` request in Postman. By running this request, you will be calling the [`IssuerSetup` endpoint](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0/#/IssuerSetup) that creates Issuer DID and Verkey pair. After you receive a response from Verity with your Issuer DID and Verkey (`did` and `verKey` in the response), please send these values to [support@evernym.com](mailto:support@evernym.com) to have them endorsed. We will inform you when we process your request.

Example of the response for the `Setup Issuer` call:

```json
{
    "identifier": {
        "did": "4vQ7WDWc76BpENZmg9b8j2",
        "verKey": "38wWqTAqbHDvvuLkyM9hqEN5WxEa2KaeDkvr2E6yuXhh"
    },
    "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created",
    "@id": "56c7920c-9a27-4e40-a87b-d97ac41b7423",
    "~thread": {
        "thid": "20734939-a88c-491d-a2c3-a7ec6d68a258 "
    }
}
```

If you experience any issues following this document, please [contact support](mailto:support@evernym.com).
