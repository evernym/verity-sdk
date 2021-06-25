# Release notes - Verity 2.12.2 (0.4.123069613.a21b28b) - Jun 16 2021

## Bugs
    *[VE-2667] - vcx connection issue

## Tasks
    *[VE-2614] - Remove sms endpoint and related configuration
    *[VE-2537] - Use a unique resource key for each message type
    *[VE-2042] - Creating the right configuration for the throttle limits we previously defined

# Release notes - Verity 2.12.1 (0.4.122738797.8b2e9a7) - Jun 16 2021

## Tasks
    *[VE-2656] - add support in data retention feature to allow deletion of data once protocol is completed

# Release notes - Verity 2.12.0 (0.4.122576192.7d4abd6) - Jun 16 2021

## Bugs
    * [VE-2643] - Prod VAS: error during persisting actor event MultiEvent: java.lang.NullPointerException
    * [VE-2642] - All Verity-Sdk wrappers should support all 4 predicate types \(">=", ">", "<", "<="\)
    * [VE-2637] - make pushId: ComMethodDetail in GetToken optional
    * [VE-2615] - In some cases errors during write schema are treated as success
    * [VE-2602] - Verity often does not send signal message back when the payload is incorrect.
    * [VE-2598] - Scanning the same oob-request-attach QR code twice cause protocol crash/restart

## Task
    * [VE-2660] - Puppet work for on demand data retention
    * [VE-2648] - Improve testing speed and output
    * [VE-2644] - backport iata-hotfix changes part-2
    * [VE-2640] - Move unneeded verity main resource config files to verity test or integration test module as needed
    * [VE-2632] - Optimize message size limits integration tests
    * [VE-2627] - Refactoring RoutingAgent actors such that it is scalable
    * [VE-2620] - Disable HttpsSupport trait as that is not being used anyhow in verity code
    * [VE-2619] - make sure all catch clauses are exhaustive else it may throw MatchError which may include secure/private information
    * [VE-2618] - Refactor RequestMsgContext and its usage
    * [VE-2617] - Fix unhandled message type logging in AgentMsgProcessor to not log any secure/private information
    * [VE-2606] - backport iata-hotfix changes part-1
    * [VE-2601] - Decide and implement data-retention for the Relationship protocol
    * [VE-2574] - Ensure S3API is using correct future dispatcher
    * [VE-2566] - Puppet work
    * [VE-2536] - Make it possible to differentiate targets for "all messages" and "all endpoints"
    * [VE-2499] - Implement For all protocols
    * [VE-2498] - Retrieving/Reading Data
    * [VE-2497] - Storing Data
    * [VE-2496] - Verity Configuration
    * [VE-2494] - Storage Interface Refactoring
    * [VE-2493] - AWS Bucket Creation
    * [VE-2334] - Migrate thread context to protocol actor state
    * [VE-2293] - Fix wallet actor performance integration test and make it run as part of CI 

# Release notes - Verity 2.11.1 (0.4.121264702.2e0e555) - May 25 2021

## Bugs
    * [VE-2664] - add more test around data retention at integration level
    * [VE-2628] - Goal Code and Goal should be optional parameters in the Verity API
    * [VE-2621] - Change ledger svc code so that status details errors are Future.failure and not success

# Release notes - Verity 2.11.0 (0.4.118200948.54709ad) - May 12 2021

## Bugs
    * [VE-2609] - make sure invitation is not re-usable once it is accepted by one user
    * [VE-2607] - prod CAS: unhandled ServiceUnavailableException
    * [VE-2590] - fix retry issue with OutgoingMsgSender
    * [VE-2559] - CAS connection failure with 3 nodes
    * [VE-2533] - Fix sync response issue with get-status of update-configs 0.6 protocol
    * [VE-2473] - Increased number of errors on Demo VAS after Verity release
    * [VE-2450] - Resource usage violation actions with track-by=user are not triggered when expected
    * [VE-2396] - Invalid replayed event, multiple persistent actor instances with same persistence id?

## Tasks
    * [VE-2613] - Add libmysqlstorage to install script
    * [VE-2612] - Intermittent failures during unit tests
    * [VE-2593] - Add entries for VS Code/Scala Metals to .gitignore file
    * [VE-2557] - Remove dependencies on bintray in Verity pipelines
    * [VE-2552] - Move off of bintray repos
    * [VE-2535] - Make it possible to target all messages specifically coming from "my domain" or from "their domain"
    * [VE-2527] - Indentify Verity dependencies
    * [VE-2514] - Upgrade verity-sdk docker image and packages to use Ubuntu 18.04
    * [VE-2490] - Make it possible to run clustered Verity in docker
    * [VE-2488] - Fix local run failures of Verity integration tests
    * [VE-2487] - Fix problem with non-steady network traffic in issuance test
    * [VE-2486] - Make libindy wallet connections limit configurable in ephemeral envs
    * [VE-2475] - Exceptions from libindy should be properly logged
    * [VE-2469] - Investigate reasons of increased DynamoDB usage in load tests
    * [VE-2462] - "Actor not found" error during verification load test
    * [VE-2457] - Verity integration tests error handling improvement
    * [VE-2437] - Increase ask timeout to 30 seconds
    * [VE-2289] - Replace the @type decorator from using did:sov:12345678abcd…. to http://didcom.org

# Release notes - Verity 2.10.1 (0.4.116677980.e8b6f05) - Mar 25 2021

## Bugs
    * [VE-2481] - fix 'needs-endorsement' broken flow
    * [VE-2477] - fix agent provisioning new key creation in agency agent wallet issue

## Tasks
    * [VE-2471] - Add agent provisioning 0.7 support for libvcx

# Release notes - Verity 2.10.0 (0.4.115809643.e5a8582) - Mar 17 2021

## Bugs
    * [VE-2357] - Webhook URL does not support query strings
    * [VE-2375] - invalid com method was switching Verity app state to degraded state
    * [VE-2378] - GNR-100 ("system is unusually busy, try again later") returned on incorrect payload
    * [VE-2382] - Wrong version of libmysqlstorage breaks Verity upgrade
    * [VE-2389] - Verity is not returning Agency Agent Verkey
    * [VE-2391] - Malicious payloads to connecting endpoint crashes Verity
    * [VE-2392] - Malicious large payload to  "~for_relationship" crashes Verity
    * [VE-2393] - SourceId: 1 crashes Verity
    * [VE-2397] - Malicious credOffer message caused Verity crash
    * [VE-2401] - oob-with-request-attach: Verifier needs to perform IssuerSetup so that connection reuse could work
    * [VE-2419] - Fix protocol async service implementation closing over state issue
    * [VE-2430] - Verity SDK i18n issues
    * [VE-2441] - auth key referenced from 'endpoints' not exists in 'authorizedKeys'
    * [VE-2447] - Fix agent provisioning protocols to use async wallet api

## Tasks
    * [VE-2234] - Add separate metric for time spent in LibIndy callbacks
    * [VE-2301] - Adding proper supervisor strategy for persistent actors
    * [VE-2326] - Migrate blocking WalletActor calls to asynchronous calls-Phase2
    * [VE-2332] - Convert wallet access in protocols to follow the async design in the Url Shortener
    * [VE-2359] - Verity cache size caps
    * [VE-2365] - Audit Verity Code base for Unbounded large collections
    * [VE-2366] - Remove old wallet API from Verity
    * [VE-2383] - Implement caching for Ledger calls for Verity
    * [VE-2403] - Switch Verity SDK sample apps to use OOB protocol
    * [VE-2404] - Switch Verity REST API demo web app to use OOB protocol
    * [VE-2405] - Switch Issuer/Verifier Verity REST API sample apps to use OOB protocol
    * [VE-2422] - Create sample app for out-of-band with request attach that uses Verity REST API
    * [VE-2438] - Change our supervisor settings to retry after 1 second instead of 3 seconds
    * [VE-2439] - Refactor ItemContainer cleanup code to be more soften instead of trying to delete all events in one go
    * [VE-2440] - remove "cache initialization info log" and "ledger svc unutilized caches"
    * [VE-2452] - DynamoDB errors during connections load test
    * [VE-2456] - Allow Verity to switch between 'libindy' and 'libindy-async'
    * [VE-2427] - Remove legacy public identity behaviour config option
    * [VE-2435] - Add more logging for http request observability

# Release notes - Verity 2.9.0 (0.4.112853588.bf9f776) - Feb 10 2021

## Bugs
    * [VE-2304] - DomainId is used as the public DID in connection invites. 
    * [VE-2346] - Prevent Kamon Timer warnings from overwhelming the logging system.

## Tasks
    * [VE-2118] - Check .NET SDK with the Verity integration tests
    * [VE-2232] - Migrate blocking WalletActor calls to asynchronous calls: Phase1
    * [VE-2246] - Add asynchronous API to protocol engine
    * [VE-2320] - Investigate and fix problem with too many open sockets
    * [VE-2336] - Expose msgType for Aries protocols in a way that doesn't break push Notifications for Connect.Me
    * [VE-2340] - Review kamon metrics/reporter for any possible improvments
    * [VE-2355] - Add support for Configurable Message Retention
    * [VE-2361] - Switch default wallet impl to `standard`
    * [VE-2373] - Add `INFO` log messages for Push Notfications

# Release notes - Verity 2.8.0 (0.4.110434998.b502641) - Jan 13 2021

## Bugs
    * [VE-2281] - Not possible to provision a token in .NET SDK

## Tasks
    * [VE-2017] - Define Messages for BasicMesasge (Control, Signal, Protocol)
    * [VE-2018] - Implement Protocol Logic for BasicMessage
    * [VE-2019] - Implement Integration tests for BasicMessage
    * [VE-2020] - Wire new Protocol for BasicMessage
    * [VE-2021] - Implement BasicMessage protocol in Verity-SDK (java, python, node, .NET)
    * [VE-2022] - Add required documentation for Basic Message
    * [VE-2046] - Expose LibIndy instrumentation as custom metrics in Verity
    * [VE-2138] - Add connection-reuse flow for OOB with request attach 
    * [VE-2249] - Catch up .NET SDK with the other SDKs (add Propose proof and names restriction changes)
    * [VE-2287] - Write Schema and Write CredDef should support "endorsing flow"

# Release notes - Verity 2.7.0 (0.4.107364822.a2523bb) - Dec 9 2020

## Bugs
    * [VE-2133] - Verity does not return attribute values in presentation-result if names syntax is used

## Tasks
    * [VE-2010] - Add integration test for proposed presentation to verity
    * [VE-2220] - Create public Dockerhub image for Verity sample app
    * [VE-2244] - Provision Token delivered via push notification and HTTP response

# Release notes - Verity 2.6.0 (0.4.105917705.a19f4a0) - Nov 25 2020

## Tasks
    * [VE-2008] - Add protocol messages for proposed presentation to verity
    * [VE-2009] - Add protocol logic for proposed presentation to verity
    * [VE-2011] - Add required APIs to verity-sdk
    * [VE-2093] - Added OOB with request-attach to Integration Tests
    * [VE-2096] - Document new API - OOB with request attach
    * [VE-2116] - Update getting started guides for .NET SDK
    * [VE-2117] - Making environment for build and publication on the NuGet
    * [VE-2120] - Update Verity SDK CI/CD pipelines for .NET 
    * [VE-2124] - Remove 0.6 Provisioning Protocol 
    * [VE-2125] - Return provision token in http response if NOT push notification format

# Release notes - Verity 2.5.0 (0.4.103141332.ef1e3e6) - Oct 21 2020

## Bugs
    * [VE-1968] - Python SDK example app sometimes throws libindy warning
    * [VE-1997] - If label is not specified in Relationship create call, Python and Java SDKs do not default to the label specified in UpdateConfigs
    * [VE-2015] - UPDATE_CONFIG protocol setting the logoUrl to null generates a NullPointerException and then crashes the UserAgent actor
    * [VE-2057] - Not possible to specify the walletPath in public constructors for NodeJS SDK

## Stories
    * [VE-1564] - Validation of SMS phone number format
    * [VE-1779] - Create Sample Web App demoing Connection-reuse

# Release notes - Verity 2.4.0 (0.4.101396269.459d5b9) - Sep 29 2020

## Bugs
    * [VE-1974] - Cannot specify self attested values during Prover's Interaction in Present Proof 
    * [VE-1976] - Trust-ping response can not be mapped to relationshipDID which sent the ping
    * [VE-1990] - publicDid missing in case of SMS connection redirect for old (0.5 and 0.6 protocols)

## Stories
    * [VE-1528] - As Evernym Verity Customer, I want to send Connection invites v1.0 using SMS to Connect.Me

# Release notes - Verity 2.3.1 (0.4.100298448.69d2cf3) - Sep 18 2020

## Bugs
    * [VE-1966] - Verification result "ProofUndefined" in presentation-result message
    * [VE-1969] - Add threadId() for WriteSchema protocol in all 3 SDKs
    * [VE-1970] - Problem with push notifications with Aries protocols

## Stories
    * [VE-1967] - When protocol receives unexpected message (for state) it should respond with problem-report.
    * [VE-1975] - Add message type to outer layer of aires outgoing messages
    * [VE-1977] - Short URL returns c_i in the response body

# Release notes - Verity 2.3.0 (0.4.99581977.9703ac5) - Sep 8 2020

## Bugs
    * [VE-1957] - No linkage between relationship-reused thid/pthid and OutofBand invitation message thid/@id
    * [VE-1958] - profileUrl is not set when out-of-band-invitation is used

## Stories
    * [VE-1491] - As Evernym Customer using Aries 1.0 connections protocol, I need to have be able to send a connection using URL
    * [VE-1811] - Verity Application needs to persists events in docker container
    * [VE-1883] - Implement support for goal-code and goal in Out-Of-Band protocol on Verity

# Release notes - Verity 2.2.1 (0.4.98503368.232c874) - Sep 2 2020

## Bugs
    * [VE-1925] - Fix legacy agent routes (found issues during release)
    * [VE-1926] - relationship data model validation issues

# Release notes - Verity 2.2.0 (0.4.98263760.043e6b5) - Aug 26 2020

## Bugs
    * [VE-1667] - Field verification_result in presentation-result message has a wrong value
    * [VE-1829] - Question answer should use url_safe base64 encoding.
    * [VE-1899] - Example app crashes when Verity Application endpoint with trailing slash is specified
    * [VE-1900] - Fix example app output once if expired token is used.

## Stories
    * [VE-1551] - Cache used provisioning tokes on Verity so token cannot be used more than once within the token window
    * [VE-1790] - Publish verity intergration test runner

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
