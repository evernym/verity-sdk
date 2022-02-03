Note: Additional useful information is contained in the release notes for dependencies such as [the Verity Service](https://gitlab.com/evernym/verity/verity/-/blob/main/CHANGELOG.md) and [VDR Tools](https://gitlab.com/evernym/verity/vdr-tools/-/blob/main/CHANGELOG.md).


# Release notes - Verity SDK 0.6.0 released 26 January 2022
Built with VDR Tools 0.8.4

## Features
* Update the sample apps to point to a QR code generator
* Various documentation updates
* [Added a sample app for issuers](https://gitlab.com/evernym/verity/verity-sdk/-/tree/fe8597b5111fb4352bd15919b79e9b8d3d999393/samples/new-customers/rest-api/issuer)
* [Added a sample app for out-of-band with request-attached](https://gitlab.com/evernym/verity/verity-sdk/-/tree/fe8597b5111fb4352bd15919b79e9b8d3d999393/samples/new-customers/rest-api/issuer)
* [Updated the list of IP addresses used for webhook responses.](https://gitlab.com/evernym/verity/verity-sdk/-/blob/583193fb52eba410a77458f76f5c0c6a84fbee9a/docs/Environments.md)

## Bugs
* [VE-3310] Fix null pointer error when running the Java sample app, caused by the VDR Tools Java Wrapper


# Release notes - Verity SDK 0.5.0 released 27 October 2021
Built with VDR Tools 0.0.5

## Features
* Replace LibIndy with [VDR Tools](https://gitlab.com/evernym/verity/vdr-tools/)
* Various other documentation improvements
* [VE-2769] Add support for [authentication of webhook endpoints using OAuth](https://gitlab.com/evernym/verity/verity-sdk/-/blob/583193fb52eba410a77458f76f5c0c6a84fbee9a/docs/AuthenticatingWebhooks.md)
* Pull the root certificate in the dockerfile from an environment variable
* Use the new Sovrin endorsement flow since self-registration as an endorsement was disabled
* Fixed various errors reported by SAST tools and linters
* Added a [guide for New Customers](https://gitlab.com/evernym/verity/verity-sdk/-/tree/f3c9db2dbd09f115b7f4ec26308720eaa9a00849/docs/new-customers)
* Added [documentation on how to manage multiple Verity tenants from a single application](https://gitlab.com/evernym/verity/verity-sdk/-/blob/facd29c6251f952d398409388677bd0efc5cd748/docs/howto/Developing-Multi-Tenant-Applications.md).
* Improvements to the UI of the sample web applications


# Release notes - Verity SDK 0.4.9 released 19 August 2021
Built with LibIndy 1.16.0

## Features
* Various additions to documentation
* [VE-2600] Be able to override endorser DID when writing to the Sovrin ledger
* ssi-auth sample app supports using the Issuer DID in the public_did field for returning users to reuse their previous connection

## Tasks
* [VE-2702]	Update verity-sdk dependencies
* Various bug fixes in sample apps and SDK wrappers


# Release notes - Verity SDK 0.4.8 released 14 June 2021
Built with LibIndy 1.15.0

## Bugs
* [VE-2642] All Verity-Sdk wrappers should support all 4 predicate types (">=", ">", "<", "<=")

## Tasks
* Pipeline improvements
* Refactoring


# Release notes - Verity SDK 0.4.7 released 18 March 2021

## Features
* [VE-2403] Switch Verity SDK sample apps to use OOB protocol
* [VE-2404]	Switch Verity REST API demo web app to use OOB protocol
* [VE-2405]	Switch Issuer/Verifier Verity REST API sample apps to use OOB protocol
* [VE-2422] Create sample app for out-of-band with request attach that uses Verity REST API

## Bugs
* [VE-2357] Webhook URL does not support query strings
* [VE-2430] Fixed Verity SDK i18n issues

## Tasks
* Add i18n support for sample apps in docker
* Dependency updates


Earlier versions of the Verity SDK were released together with Verity, and so are included in [the Verity Release Notes](https://gitlab.com/evernym/verity/verity/-/blob/main/CHANGELOG.md).
