# Release notes - Verity 2.1 (0.4.95258837.49f840f) - July 28 2020

## Bugs
    * [VE-1457] - Node.Js Verity SDK bug with uuid dependency
    * [VE-1542] - Committed Answer protocol not working on the Verity 2.0 (0.4.91738644.3fe8dde)
    * [VE-1559] - Acme Corp (try.connect.me) should be using proper logo instead of the robohash logo
    * [VE-1581] - Verity should be using Enterprise name and logo from the UpdateConfigs call in the ConnectionInvitation
    * [VE-1605] - DynamoDB high read condition
    * [VE-1659] - Endpoint URLs containing IP addresses fail URL validation
    * [VE-1667] - Field verification_result in presentation-result message has a wrong value
    * [VE-1637] - Faber/Alice demo broken on team1 EAS 
    * [VE-1500] - Fix problem with too much logging in CAS

## Tasks
    * [VE-1540] - As Evernym TE, I need to add puppet config for enforcing token provisioning on VAS
    * [VE-1554] - Change REST API token provisioing script to use 0.7

## Stories
    * [VE-1322] - As Verity customer, I want to be able to issue credential in one step (instead of two)
    * [VE-1412] - Create comprehensive Verity SDK documentation
    * [VE-1458] - Design and Implement connection redirect (or connection reuse) on Verity using Aries protocols
    * [VE-1536] - Log wrongly formatted REST API payload
    * [VE-1547] - As Evernym Customer, I want access a source code of web app based on vcx-customer toolkit
    * [VE-1557] - As Evernym  Verity SDK customer, I want to install Verity SDK from language specific repos
    * [VE-1568] - As Evernym  Verity SDK customer, I want a way to easily run example apps using Docker
    * [VE-1621] - I want to configure a custom SMS text message per customer
    * [VE-1590] - Add support for multiple sponser app to be able to use push notification service
    * [VE-1672] - Tokenizer use sponsor push service

# Release Notes - Verity version 2.0 (0.4.91738644.3fe8dde) - Jun 17 2020

## Bugs
    * [VE-1395] - Fix memory leaks issues
    * [VE-1444] - indy-sdk logging--should explicitly set logging to INFO not TRACE

## Stories
    
### Multi-tenancy
    * [VE-1147] - As Evernym, we can provision multiple customers (Issuer Identities or tenants) to one Verity server instance
    * [VE-1121] - As Verity SDK customer, I'm able to register (perform) provisioning of multiple SDK instances to my one Verity Server so I can have multiple client codes using one Verity Server
    * [VE-1316] - As Verity REST customer, I'm able to register more than one REST client to Verity instance
    * [VE-1148] - As Evernym, we need to be able to differentiate different tenants on one Verity Server (Customer Identifier)
    
### Aries
    * [VE-1538] - Implement Trust Ping AIP v1 protocol on Verity
    * [VE-1378] - Implement REST API for Aries protocols

### Other
    * [VE-1381] - Improve Verity SDK documentation
    * [VE-1409] - Update UPDATE_COM_METHOD protocol
    * [VE-1504] - REST json responses from http endpoint should have content-type set to application/json instead of text/plain
    * [VE-1490] - As Evernym, I need to update Acme corp of try.connect.me to use Aries (1.0) protocols and new SDK

# Release Notes - Verity version 1.8 (0.4.88971934.01eadf6) - May 11 2020

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
