# Developing Multi-Tenant Applications

It is possible that during the development of a Verity-Sdk application developers will wish to create a single application which will manage multiple contexts and/or wallets, perhaps to represent the different users of the application. This guide goes over the key features of verity-sdk which enable such a use case. The Python Sdk will be used for partial example code, but this information applies equally to all the native Verity-Sdk's. This does NOT apply to Verity REST API's, though this same functionality is possible there as well. For the purposes of this guide, applications with multiple wallets and/or contexts will be called multi-tenant applications. 

The key feature to understand for developing multi-tenant applications is the Verity Context object. The Verity Context manages which wallet and what DID's and keys will be used for a given protocol. A context can be built many ways, but the most common is either as part of the [Provision protocol](https://developer.evernym.com/doc/python/0.4.0/protocols/v0_7/Provision.html) 
```python
# create initial Context
context = await Context.create(WALLET_NAME, WALLET_KEY, verity_url)

# ask that an agent by provision (setup) and associated with created key pair. The Response will be a new context json object
response = await Provision(token).provision(context)
```
or reusing an existing context
```python
global config
with open(CONFIG_PATH, 'r') as f:
    config = f.read()
context = await Context.create_with_config(config)
```

Each new context will need an agent provisioned for it before it can be used. This only needs to happen once, but be sure and store the provided context as there is no way to recover it if it is lost. 

When creating a context the provided wallet name and key will be used to determine which local wallet the context will use for wallet operations. If no wallet exists with the given name, a new wallet will be created at ~/.indy_client/wallet. A custom location for the created wallet can be defined using the wallet_path parameter when creating or opening the wallet. 

## Context Operations

Managing contexts and wallets is done mostly through the use of functions in the context object itself. A full list with descriptions of all parameters is available [here](https://developer.evernym.com/doc/python/0.4.0/utils/Context.html), but some important operations to know will be:

### create
Creates a new context object, and a new wallet with the given wallet name and wallet key at the specified wallet path. If no path is specified the default will be ~/.indy_client/wallet/. 

### create_with_config
Creates a context object from a json object. This can be used to recreate a previously created context within application code. The passed json object should be a previous context that was converted to json. 

### close_wallet
Closes the currently opened wallet for a given context. Each context may only have one open wallet at a given time.

## Protocols
When protocol commands are executed a context object must be specified. The provided context will determine what agent and wallet the protocol will be executed for. Details for all available protocols can be found [here](https://developer.evernym.com/doc/python/0.4.0/protocols/index.html)