# SDKs for Verity

See individual SDK implementations for more information and examples.

## Up to date SDKs

* Java
* Python
* Node.js

## Out of date or work in progress SDKs

* browser-sdk

## Note on Provisioning and the Resulting Config

In order to initialize the SDK, the tools/provision_sdk.py script must first be run.  It outputs a config like the following: 

```js
{
  "sdkPairwiseVerkey": "EiTU3kFD6jPuFGnBiDrKDwSRSXbPBcR1sNqDx8G5isBc", // Your public key used only with Verity
  "verityPairwiseVerkey": "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL", // Verity's public key used only with you
  "verityPublicVerkey": "JSA3k3kafsakljasdflkjasdlkfjaslfkj33jktewekasd", // Verity's public key used with everyone
  "verityUrl": "http://localhost:8080/msg", // The url messages to Verity should be POSTed to
  "walletKey": "12345", // The wallet key (specified when provisioning)
  "walletName": "wallet", // The wallet name (specified when provisioning)
  "endpointUrl": "http://localhost:4000" // The url your application is listening for messages on
}
```

Somewhere in your application you must use this config (probably stored in a json config file) to initialize Verity SDK. For how to do that, please refer to the individual SDKs.