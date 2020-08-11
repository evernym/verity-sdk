# Verifier Documentation
Here are basic code examples showing how to interface with verity-sdk to: 
1. Create an Agent on Verity - [Provision](../howto/python-verity-flow.md#Provisioning agent on Verity)
2. Handle asynchronous response messages from Verity - [Message Handling](../howto/python-verity-flow.md#Handling Asynchronous response messages)
3. Setting up an Issuer - [Issuer Setup](../howto/python-verity-flow.md#Setting up an Issuer identity)
4. Writing a schema to the ledger - [Write Schema](../howto/python-verity-flow.md#Write Schema to Ledger)
5. Writing a credential definition to the ledger - [Write Credential Definition](../howto/python-verity-flow.md#Write Credential Definition to Ledger)
6. Establishing Connections between parties - [Connecting](../howto/python-verity-flow.md#Connecting)
7. Issuing credentials - [Issue Credential](../howto/python-verity-flow.md#Issue Credential)
8. Requesting Proof Presentations - [Request Proof Presentation](../howto/python-verity-flow.md#Request Proof Presentation)
9. Utils for saving verity-sdk context and registering Message Handlers  - [Utils](../howto/python-verity-flow.md#Utils)

## Setup
### Provisioning agent on Verity
Provisioning is the first step done when interacting with Verity. It creates a dedicated cloud agent on Verity for the user of the sdk. Provisioning is done only once.

```python
global context
wallet_name = 'examplewallet1'  # for libindy wallet
wallet_key = 'examplewallet1'
#token used for provisioning - Evernym provides this offline for their customers
token = '....' 
verity_url = 'http://verity.url'
# create initial Context
context = await Context.create(wallet_name, wallet_key, verity_url)

# ask that an agent by provision (setup) and associated with created key pair
context = await Provision(token).provision(context)

# Save context 
with open('verity-context.json', 'w') as f:
    f.write(context.to_json())
```
The wallet (usualy created in $HOME/.indy_client/wallet/<wallet-name>) needs to be saved with the context file.

## Handling Asynchronous response messages
### Setting up Webhook
Most Verity response messages are sent asynchronously. For receiving messages, a public endpoint is needed. The UpdateEndpoint protocol should be used for setting up the address of this endpoint.
The endpoint dedicated for receiving messages from Verity Server, may look like this:

```python
# Needed if an updated endpoint is used

global context, port
webhook: str = context.endpoint_url

context.endpoint_url = webhook

# request that verity application use specified webhook endpoint
await UpdateEndpoint().update(context)

# Write the context with the updated endpoint
with open('verity-context.json', 'w') as f:
    f.write(context.to_json())
```

Example Webhook
- This example uses the Java Spring Framework but Java Spring is not required for verity-sdk.
```python
@routes.post('/')
async def endpoint_handler(request):
    try:
        await handlers.handle_message(context, await request.read())
        return web.Response(text='Success')
    except Exception as e:
        traceback.print_exc()
        return web.Response(text=str(e))
```

### Response message handling 
Most Verity interactions respond to a request asynchronously. Here are some details that will help with the handling of these messages.
1. A response message is delivered via HTTPs. These messages can be processed however the application thinks best. Our example applications use webhooks.
    The http body will contain an encrypted protocol message which needs to be handled by the [Handlers](../howto/python-verity-flow.md#Registers Message Handler) object. Decryption of the message happens here.
    ```python
    await handlers.handle_message(context, await request.read())
    ``` 
2. Common Fields which show up in a response: 
   - `@type` - \<did info>;spec/\<message family>/\<version of protocol>/\<protocol message>" 
    > **Example:** "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created"
   - `@id` - An assigned message identifier
   - `~thread`: `{"thid":"<id>"}`
   - Message specific fields
3. Example handler: 
    - Registering the handler [Registers Message Handler](../howto/python-verity-flow.md#Registers Message Handler)
    - Protocol Message handlers: 
        > **NOTE:** The MessageFamily in this example is an instance of ConnectionsV1_0.
    ```python
   # Adding the message handler to list of handlers
   handlers.add_handler(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, connection_resposne_handler)

    # handler for the accept message sent when connection is accepted
    async def connection_response_handler(msg_name, message):
        print_message(msg_name, message)
        if msg_name == 'response-sent':
           # Message Handling 
        else:
           # No handler for response message (msg_name, message)
    ```

## Setting up an Issuer identity
When an entity wants to issue a credential, they need to have privileged keys on the ledger. This is the step to create \
the issuer keys and register them on the dedicated cloud agent so that writing to the ledger and issuing credentials can be accomplished.
### Check to see if Issuer is already setup
Checks to see if issuer setup has been done. Gets did and verkey from the Verity Application
```python
# constructor for the Issuer Setup protocol
issuer_setup = IssuerSetup()

# 1. query the current identifier
# 2. application's message handler should handle the asynchronous response
await issuer_setup.current_public_identifier(context)
```

Message Response: 
* type: `public-identifier`
    ```json
    {
      "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier",
      "~thread": {"thid": "51e74fef-16e2-4f52-b54d-bc868418beda"},
      "@id": "e497da01-f907-43b9-8607-68d94f83a22a",
      "verKey": "GoNQz4LV4jUCxUZ9rBoBptc5wtPzyYAg4WDV2GQrT3rA",
      "did": "VzJn8BMiQDhitA7BSmTthe"
    }
    ```
* Save values: \
    These are associated with all future credential definitions and issued credentials.
    - `did`: newly created issuer did
    - `verkey`: newly created issuer verkey

### Setup new Issuer
```python
# constructor for the Issuer Setup protocol
issuer_setup = IssuerSetup()

# add handler to the set of handlers

# 1. request that issuer identifier be created
# 2. application's message handler should handle the asynchronous response
await issuer_setup.create(context)
```

Message Response: 
* type: `public-identifier-created`
    ```json
    {
      "identifier": {
        "verKey": "GoNQz4LV4jUCxUZ9rBoBptc5wtPzyYAg4WDV2GQrT3rA",
        "did": "VzJn8BMiQDhitA7BSmTthe"
      },
      "@type": "did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created",
      "~thread": {"thid": "1d90666d-a440-4230-b381-cf10f34ded5d"},
      "@id": "2305b750-9eb2-449f-bb82-6393f6b25f65"
    }
    ```
* Save values: \
    These are associated with all future credential definitions and issued credentials.
    - `did`: newly created issuer did
    - `verkey`: newly created issuer verkey

## Updating Config
Update agent configs to set the institution's name and logo url. 
```python
INSTITUTION_NAME = 'Faber College'
LOGO_URL = 'http://robohash.org/235'
handlers.add_handler('update-configs', '0.6', noop)
configs = UpdateConfigs(INSTITUTION_NAME, LOGO_URL)
await configs.update(context)
```

## Write Schema to Ledger
When data is going to be shared via credential exchange, the data needs to be publicaly defined. 
This is done by writing a schema to the ledger. Different issuers can create credentials that use this defined Schema. [Issuer Setup](../howto/python-verity-flow.md#Setting up an Issuer identity) must be complete to have the proper permissions.
```python
# input parameters for schema
schema_name = 'Diploma'
schema_version = '0.1' # get_random_version()
schema_attrs = ['name', 'degree']

# constructor for the Write Schema protocol
schema = WriteSchema(schema_name, schema_version, schema_attrs)

# add handler to the set of handlers

# 1. request schema be written to ledger
# 2. application's message handler should handle the asynchronous response
await schema.write(context)
```
Message Response: 
* type: `status-report`
    ```json
    {
      "schemaId": "VzJn8BMiQDhitA7BSmTthe:2:Diploma 5361e670:0.1",
      "@type": "did:sov:123456789abcdefghi1234;spec/write-schema/0.6/status-report",
      "~thread": {"thid": "108758fa-b6d1-44a8-b108-fb382eec1823"},
      "@id": "912d82dd-f19e-4223-b07a-bbd344124705"
    }
    ```
* Save the `schemaId`. This will be used to create credential definitions. 

## Write Credential Definition to Ledger
An issuer will write a credential definition to the ledger which corresponds to a specific Schema. \
This is how an entity can publicaly define the data which will be sent in a credential.
[Issuer Setup](../howto/python-verity-flow.md#Setting up an Issuer identity) must be complete to have the proper permissions to both write to the ledger and sign data in a credential.
* `schema_id`: received in the write schema response [Write Schema](../howto/python-verity-flow.md#Write Schema to Ledger)
```python
# input parameters for cred definition
cred_def_name = 'Trinity College Diplomas'
cred_def_tag = 'latest'

# constructor for the Write Credential Definition protocol
cred_def = WriteCredentialDefinition(cred_def_name, schema_id, cred_def_tag)

# add handler to the set of handlers

# 1. request the cred def be writen to ledger
# 2. application's message handler should handle the asynchronous response
await cred_def.write(context)
```

Message Response: 
* type: `status-report`
```json
{
  "credDefId": "VzJn8BMiQDhitA7BSmTthe:3:CL:13094:latest",
  "@type": "did:sov:123456789abcdefghi1234;spec/write-cred-def/0.6/status-report",
  "~thread": {"thid": "cbeb962f-ee08-48b7-91ea-e00539357ec6"},
  "@id": "cb9976ca-e395-4cef-8d11-8b55cc9f3677"
}
```
* Save the `credDefId`. This will be used to specify the type of credential to be issued. 

## Connecting
When an entity wants to interact with another party, a connection is established. This process creates keys specifically for this interaction. Data can be requested and delivered over this channel.

### Creating an Invitation with Relationship Protocol
We create an api which will return the invitation. That invitation can be converted to QR code which can be scanned by Connect.me.
This code is just an example, it does not handle error cases.

The Relationship protocol has two steps: 
1. create relationship key 
    ```python 
   global context
   global handlers

   # Constructor for the Relationship API
   relationship: Relationship = Relationship(label='inviter')

   # add handler to the set of handlers

   # starts the relationship protocol
   await relationship.create(context)
    ```
   Message Response: 
   * type: `created`
       ```json
        {
          "@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/created",
          "~thread": {"thid": "caf7d140-966f-4cf4-a8e7-527cb6ff1e19"},
          "@id": "4704e7f9-c3af-4c51-b2b4-1f905c72166b",
          "verKey": "4YC42v4WTrMUNWv6ZFX3WVnyKAxJqHs3WUqgxJUpvQgD",
          "did": "7VUS2GgoXcm7L9bCUDzSLj"
        }
       ```
    * Save values: \
        These are associated with all future interactions for a specific relationship
        - `did`: did created for a specific relationship
        - `verkey`: verkey created for a specific relationship
2. create invitation
    - `rel_did` and `thread_id` were given in the `created` response
        ```python
        relationship: Relationship = Relationship(rel_did, thread_id)
        await relationship.connection_invitation(context)
        ```
    Message Response: 
    * type: `invitation`
       ```json
       {
         "inviteURL": "http://vas-team1.pdev.evernym.com:80/agency/msg?c_i=eyJsYWJlbCI6Imludml0ZXIiLCJzZXJ2aWNlRW5kcG9pbnQiOiJodHRwOi8vdmFzLXRlYW0xLnBkZXYuZXZlcm55bS5jb206ODAvYWdlbmN5L21zZyIsInJlY2lwaWVudEtleXMiOlsiNFlDNDJ2NFdUck1VTld2NlpGWDNXVm55S0F4SnFIczNXVXFneEpVcHZRZ0QiXSwicm91dGluZ0tleXMiOlsiNFlDNDJ2NFdUck1VTld2NlpGWDNXVm55S0F4SnFIczNXVXFneEpVcHZRZ0QiLCJFVExnWktlUUVLeEJXN2dYQTZGQm43bkJ3WWhYRm9vZ1pMQ0NuNUVlUlNRViJdLCJwcm9maWxlVXJsIjoiaHR0cDovL3JvYm9oYXNoLm9yZy8yMzUiLCJAdHlwZSI6ImRpZDpzb3Y6QnpDYnNOWWhNcmpIaXFaRFRVQVNIZztzcGVjL2Nvbm5lY3Rpb25zLzEuMC9pbnZpdGF0aW9uIiwiQGlkIjoiODQ4YTJjZGQtZTI1ZS00YTNlLWE2ZTEtMTJjNzkzZmNmOWQ2In0=",
         "@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation",
         "~thread": {"thid": "caf7d140-966f-4cf4-a8e7-527cb6ff1e19"},
         "@id": "148c5c0f-09bb-48f6-b9d0-b1c4fd04615b"
       }
       ```
   - Save value `inviteURL`. This is used to establish a connection by either delivering directly to other party or generated a QR Code.
    
### Connection Listener 
Once the mobile device receives the `inviteURL` or scans the QR Code, the rest is automated in verity-sdk. \
You can setup a handler to view incoming messages.
```python
# handler for the response to the request to start the Connecting protocol.
async def inviter_handler(msg_name, message):
    spinner.stop_and_persist('Done')
    print_message(msg_name, message)
    if msg_name == 'request-received':
        # request received
    elif msg_name == 'response-sent':
        # response sent
    else:
        # non supported response

# adds handler to the set of handlers
handlers.add_handler(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, inviter_handler)
```

## Issue Credential

When an entity wants to provide data to another party, the Issue Credential protocol is used. Both the [Issuer Setup](../howto/python-verity-flow.md#Setting up an Issuer identity) and [Write Credential Definition](../howto/python-verity-flow.md#Write Credential Definition to Ledger) protocols need to have been completed.

The Issue Credential has two steps: 

1. Send the Credential Offer
* `cred_def_id`: received in the credential definition response [Credential Definition Response](../howto/python-verity-flow.md#Write Credential Definition to Ledger)    
* `rel_did`: received in the create Relationship response [Create Relationship](../howto/python-verity-flow.md#Creating an Invitation with Relationship Protocol)
    ```python
    # input parameters for issue credential
    cred_def_id = ...
    credential_name = 'Degree'
    credential_data = {
        'name': 'Joe Smith',
        'degree': 'Bachelors'
    }

    # constructor for the Issue Credential protocol
    issue = IssueCredential(rel_did, None, cred_def_id, credential_data, 'comment', 0, True)

    # add handler to the set of handlers
   
    # 1. request that credential is offered
    # 2. application's message handler should handle the asynchronous response
    await issue.offer_credential(context)
   ```
   
2. Send the Credential once the holder sends a `accept-request` - This is automated in the sdk

## Request Proof Presentation
When an entity wants another party to prove specific things by providing self attested information or information corresponding to an already issued credential, the Proof Presentation protocol is used. 

* `issuer_did`: received in the IssuerSetup response [Issuer Setup](../howto/python-verity-flow.md#Setting up an Issuer identity)
* `for_did`: received in the create Relationship response [Create Relationship](../howto/python-verity-flow.md#Creating an Invitation with Relationship Protocol)
```python
global issuer_did

# input parameters for request proof
proof_name = 'Proof of Degree'
proof_attrs = [
    {
        'name': 'name',
        'restrictions': [{'issuer_did': issuer_did}]
    },
    {
        'name': 'degree',
        'restrictions': [{'issuer_did': issuer_did}]
    }
]

# constructor for the Present Proof protocol
proof = PresentProof(for_did, None, proof_name, proof_attrs)

# add handler to the set of handlers

# 1. request proof
# 2. application's message handler should handle the asynchronous response
await proof.request(context)
```

Message Response: 
* type: `presentation-result`
    ```json
    {
      "@type": "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/presentation-result",
      "~thread": {
        "received_orders": {"YaLZ14xqPQGErywdZarBgd": 0},
        "sender_order": 0,
        "thid": "725dab3b-3f87-4e8d-a7f0-11f4ef3a56af"
      },
      "verification_result": "ProofValidated",
      "@id": "8318b788-c6f8-438e-93ed-f00e9e1b5f3c",
      "requested_presentation": {
        "predicates": {},
        "self_attested_attrs": {},
        "identifiers": [{
          "schema_id": "VzJn8BMiQDhitA7BSmTthe:2:Diploma 5361e670:0.1",
          "cred_def_id": "VzJn8BMiQDhitA7BSmTthe:3:CL:13094:latest"
        }],
        "revealed_attrs": {
          "name": {
            "identifier_index": 0,
            "value": "Joe Smith"
          },
          "degree": {
            "identifier_index": 0,
            "value": "Bachelors"
          }
        },
        "unrevealed_attrs": {}
      }
    }
    ```
    * to see if the presentation is valid, evaluate `verification_result`
    
    
## Utils
### Object used to register response message handlers
The `handlers` variable is defined as field variable in the controller class like this:
<a name="Handlers"></a>
```python
handlers: Handlers = Handlers()
```
### Registers Message Handler
Sets a specific response handler for protocol interactions
<a name="handle"></a>
```python
await handlers.handle_message(context, await request.read())
``` 
### Loading Context Object
Saved context should be loaded with code like this:
```python
def load_context(file_path) -> str:
    with open(file_path, 'r') as f:
        config = f.read()
        context = await Context.create_with_config(config)
        return context
```
Example Context Object: 
```json
{
  "verityPublicVerKey": "ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV",
  "verityUrl": "https://vas-team1.pdev.evernym.com",
  "verityAgentVerKey": "ZT8HE1t4eF2iMm6x8a2fPjPM1TiY2vuBZ8BMejL3Q9a",
  "walletKey": "examplewallet1",
  "sdkVerKey": "DNZ9Yw2bowKkjCZ1cuX8o4UmTMYNSjSsw5cszuTaPjLz",
  "walletName": "examplewallet1",
  "endpointUrl": "http://4153716fd8e9.ngrok.io",
  "verityPublicDID": "Rgj7LVEonrMzcRC1rhkx76",
  "sdkVerKeyId": "PhXp3RnCSinuCZsqYmy15T",
  "version": "0.2",
  "domainDID": "KWyJwot75jqnGAH1P5jioe"
}
```
