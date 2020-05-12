# Release Notes - Verity Version 1.8 (0.4.88971934.01eadf6) - May 11 2020



## Bugs
    * [VE-1382] - Public DID in the Connection Invite is wrong one for both SDK and REST API
    * [VE-1390] - Connection get status missing sourceId


## Stories

### Aries
    * [VE-1066] - Implement Aries protocols for connecting/issuing/proving in Verity

### REST API
    * [VE-1151] - Issuer setup REST API
    * [VE-1153] - Create Schema REST API
    * [VE-1154] - Create cred def REST API
    * [VE-1155] - Connection establishment (DID exchange) REST API
    * [VE-1156] - Issue credential REST API
    * [VE-1157] - Request and validate proof REST API
    * [VE-1158] - Send and receive Committed Answer REST API
    * [VE-1159] - Retrieve Connection Invite protocol status REST API
    * [VE-1161] - Retrieve Request and validate proof protocol status REST API
    * [VE-1162] - Retrieve Send and receive Committed Answer protocol status REST API
    * [VE-1326] - Organization name&logo REST API
    * [VE-1327] - Configure Webhook REST API call
    * [VE-1357] - Create OpenAPI (formerly know as swagger) documentation for REST API
    * [VE-1363] - Implement throttling on REST API endpoints on Verity
    * [VE-1386] - Create python script for provisioning API keys for REST API

### Verity SDK
    * [VE-1403] - Add a method for creating REST API key for existing Verity SDK users 
    * [VE-1406] - Update Getting Started with Verity SDK

### Other
    * [VE-1402] - Add resource usage block on the PROVISION protocol
    * [VE-1418] - Add msg response time related 'span' metrics for all protocol messages
    * [VE-1364] - Metrics (latency and response times) on Verity REST endpoints
