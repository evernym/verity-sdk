v0.1

# Verity Application Protocol Extensions Doc 

<a id="overview"></a>
## Overview

This document outlines the various definitions, message formatting, and message requirements for interacting with the verity service. This document will be  versioned and is intended to be extended and mutated as seen fit by the maintainers of the verity service. Treat this document as you would any RESTful or other api document, in that you build your messages to the service based on the guidelines below.

<a id="tableofcontents"></a>
## Table of Contents
- [Verity Application Protocol Extensions Doc](#Verity-Application-Protocol-Extensions-Doc)
  - [Overview](#Overview)
  - [Table of Contents](#Table-of-Contents)
  - [Global definitions and helpers](#Global-definitions-and-helpers)
    - [JSON and DID Comms](#JSON-and-DID-Comms)
      - [Generating attributes](#Generating-attributes)
    - [Roles](#Roles)
  - [Common](#Common)
    - [Check Status](#Check-Status)
      - [Attributes](#Attributes)
    - [Invite Request](#Invite-Request)
      - [Attributes](#Attributes-1)
    - [Invite Response](#Invite-Response)
      - [Attributes](#Attributes-2)
    - [Problem Report](#Problem-Report)
      - [Attributes](#Attributes-3)
    - [Update Communication Method](#Update-Communication-Method)
      - [Attributes](#Attributes-4)
  - [Connection](#Connection)
    - [New Connection](#New-Connection)
      - [Attributes](#Attributes-5)
      - [Notes](#Notes)
    - [Problem Report](#Problem-Report-1)
    - [Status](#Status)
      - [Attributes](#Attributes-6)
  - [Provable Question](#Provable-Question)
    - [Ask Question](#Ask-Question)
      - [Attributes](#Attributes-7)
      - [Notes](#Notes-1)
    - [Problem Report](#Problem-Report-2)
    - [Status](#Status-1)
      - [Attributes](#Attributes-8)
      - [Notes](#Notes-2)
  - [Schema](#Schema)
    - [Write Schema](#Write-Schema)
      - [Attributes](#Attributes-9)
    - [Problem Report](#Problem-Report-3)
    - [Status](#Status-2)
      - [Attributes](#Attributes-10)
  - [Credential Definition](#Credential-Definition)
    - [Write Credential Definition](#Write-Credential-Definition)
      - [Attributes](#Attributes-11)
    - [Problem Report](#Problem-Report-4)
    - [Status](#Status-3)
      - [Attributes](#Attributes-12)
  - [Credential](#Credential)
    - [Credential](#Credential-1)
      - [Attributes](#Attributes-13)
    - [Problem Report](#Problem-Report-5)
    - [Status](#Status-4)
      - [Attributes](#Attributes-14)
  - [Proof](#Proof)
    - [Proof Request](#Proof-Request)
      - [Attributes](#Attributes-15)
    - [Problem Report](#Problem-Report-6)
    - [Status](#Status-5)
      - [Attributes](#Attributes-16)
  - [Enroll](#Enroll)
    - [New Enrollment](#New-Enrollment)
      - [Attributes](#Attributes-17)
      - [Notes](#Notes-3)
    - [Problem Report](#Problem-Report-7)
    - [Status](#Status-6)
      - [Attributes](#Attributes-18)


<a id="global-defs"></a>
## Global definitions and helpers

The following are universal definitions and helpful pointers for building messages as well as understanding them.

<a id="json-and-did-comms"></a>
### JSON and DID Comms
Because of the flexibility of JSON It is expected that JSON formatting for interaction with verity server will follow [this](https://docs.google.com/document/d/1flfXrvZ0ehzB6v69VjVLJXFyITCLjla58d7SNYVNhac/edit) document and it's guidelines around DID Comm messages.

Message attributes and structure for all very protocol extensions will adhere to this [HIPE](https://github.com/hyperledger/indy-hipe/tree/master/text/0021-message-types#message-type-design-guidelines), to ensure attributes are consistent and readable.

#### Generating attributes

* `@id` This attribute is common through verity service message extension protocols and has certain unique requirements in order to properly generate an id. You can read the specs and requirements in the message-threading HIPE [here](https://github.com/hyperledger/indy-hipe/blob/613ed302bec4dcc62ed6fab1f3a38ce59a96ca3e/text/message-threading/README.md#message-ids).

* `seqnum` Following the documentation on [seqnum](https://github.com/hyperledger/indy-hipe/blob/613ed302bec4dcc62ed6fab1f3a38ce59a96ca3e/text/message-threading/README.md#sequence-num-seqnum), this attribute is often required in messages within the `~thread` attribute object. This number is **required** for any message thread over a single message per sender.

* `pthid` Nested interaction. Read [this](https://github.com/hyperledger/indy-hipe/blob/613ed302bec4dcc62ed6fab1f3a38ce59a96ca3e/text/message-threading/README.md#nested-interactions-parent-thread-id-or-pthid) HIPE for an explanation on the differences between `thid` and `pthid`

<a id="roles"></a>
### Roles

Due to the asynchronous nature of the verity service and its protocols, messages are broken up into two general types: *request messages* and *response messages*. This is based on the state machine pattern for any request response interaction defined [here](https://github.com/hyperledger/indy-hipe/blob/9c7722d208cfe0a336cb67a626cbbb192ae73f8c/text/feature-discovery/README.md#states).

* *request messages*: describe messages that the client will *send* to the verity server

* *response messages*: describe messages that the client will *receive* from the verity server

---

<a id="common"></a>
## Common

protocol: **common** | ver: **0.1** 

This protocol defines general verity service message types. This protocol is intended to provide convenience functionality that may be used across different protocols, and messages that can be passed for any reason such as error handling. For example with the `check-status` message, you may want to use that to get a status of an onboard request, and in parallel get the status of a seperate credential issuance, which are two very different protocol families, but can be queried using the same common protocol.

<a id="common:check-status"></a>
### Check Status

type: **check-status**

Request to check the status of a protocol. 

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/check-status"
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    }
}
```

#### Attributes

* `~thread` contains:
    * `thid` <@id> of the enrollment thread you want status of. 

<a id="common:invite-request"></a>
### Invite Request

type: **invite-request**

Initiates a request for the invite details of a particular connection.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/invite-request",
    "@id": <uuid>,
    "connectionDetail": {
        "sourceId": "John Doe 1234"
    }
}
```

#### Attributes

* `connectionDetail` defines the connection you would like details of, contains:
    * `sourceId` source id of the connection

<a id="common:invite-response"></a>
### Invite Response

type: **invite-response**

Provides the details of a connection

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/invite-response",
    "@id": <uuid>,
    "~thread": {
        "thid": <thid>
    }
    "invite": "I am an Invite detail for John Doe",
    "connectionDetail": {
        "sourceId": 'John Doe 1234'
    }
}
```

#### Attributes

* `~thread` contains:
    * `thid` should reference the <@id> of the request
* `invite` details of an invite, should be a string
* `connectionDetail` contains the details of the connection you queried in the first message

<a id="common:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for non-specific errors. Generally, problem reports will be handled by specific protocols (connection, cred, enroll), but sometimes a more generalized problem report needs to be generated, such as the service had a general failure, insufficient resources, or if a bad message was passed to verity service. Because these reports encompass potentially a lot of detail and use cases and error handling is rather difficult by nature, it is recommended you read [this hipe](https://github.com/hyperledger/indy-hipe/blob/d6503aebbc03b80796c6801ade9265592f9d5521/text/error-reporting/README.md#00-error-reporting) on error reporting, which the verity service models for error handling patterns, and will greatly help in building solid error handling to respond to these messages. Keep in mind that verity service will not likely use every attribute described in the hipe, but will attempt to use the key attributes that describe the error adequately.

TODO: Many errors will have error codes that live in other locations and will need to be cataloged.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>,
        "seqnum": 1
    },
    "@msg_catalog": "vs_repo/docs/errors"
    "comment": {
        "en": "internal serice error, out of memory",
        "code": 504
    },
    "who_retries": "me",
    "where": "consumer agent"
    "timestamp": 2019-06-12 13:23:06Z
}
```

#### Attributes

* `~thread` **optional** if the message is not related to a particular thread, this attribute and sub fields are redundant and can be omitted. Contains:
    * `pthid` parent thread indicating the <@id> of the triggering message
    * `seqnum` is set to 1 as the triggering message becomes `seqnum: 0`
* `@msg_catalog` **optional** resolves to an endpoint to help lookup error codes. This is marked as required in the *hipe* but as of this version is optional as it likely will be unsupported for the time being.
* `comment` field is **required**, but does not have to contain every element below, but needs **>1** attributes below. Details the error, contains: 
    * `en` **optional** human readable message for the error
    * `code` **optional** symbolic name for the error
* `who_retries` **optional** (if not added to message it is assumed retry is **none**) enum describing which party retries, if any:
    * `0` recipient of message (you)
    * `1` sender of message (me)
    * `2` both 
    * `3` none (default status)
* `where` **optional** location where the error happened. Can be turned into an enum.
* `timestamp` time of error

<a id="common:update-com-method"></a>
### Update Communication Method

type: **update-com-method**

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/update_com_method",
    "@id": <uuid>,
    "comMethod": {
        "id": "webhook",
        "type": "webhook"
        "value": <new webhook>
    }
}
```

#### Attributes
- `comMethod`
	- `value` The new webhook that verity should use to send messages to the SDK.

---

<a id="connection"></a>
## Connection

protocol: **connection** | ver: **0.1**

The *Connection* set of protocols encompass the necessary functionality to allow an enterprise to create a pairwise connection with an identity owner.

<a id="connection:new-connection"></a>
### New Connection

type: **new-connection**

Initiates the process to connect with a user

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connection/0.1/new_connection",
    "@id": <uuid>,
    "connectionDetail":{
        "sourceId": "CONN_iAmAConnId",
        "phoneNo": "8001112222",
        "usePublicDid": true
    }
}
```

#### Attributes

* `connectionDetail` contains:
    * `id` unique identifier for the connection, mapping to the enterprise connection
    * `phoneNo` **optional** *string* connection sms
    * `usePublicDid` **optional** *boolean* indicates that the connection invite should use the public DID

#### Notes

1) Connection will be usable (unless deleted?) for sending credentials and requesting proof.
2) DID comm is asynchronous so status updates will be sent in the response.


<a id="connection:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the connection protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connection/0.1/problem_report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "user rejected the connection request",
        "code": 1024
    },
    "timestamp": 2019-06-12 13:23:06Z
}
```

<a id="connection:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connection/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <pthid>,
        "seqnum": 3
    },
    "status": 1,
    "message": <status message>,
    "content": <invite_details or pairwise_did>
}
```

#### Attributes

* `status` enum that resolves to one of 2 states:
    * `0` awaiting response. (invite details in content and invite has been sent if phoneNo defined)
    * `1` invite was accepted by the user (connectionId in content)
    * `2` connection invite accepted by verity on your behalf
* `message` **optional** message relating to the status
* `content` **optional** content field associated with the status

---

<a id="provable-question"></a>
## Provable Question

protocol: **provable_question** | ver: **0.1**

The *Provable Question* set of protocols encompass the necessary functionality to allow an enterprise to ask questions through a pairwise connection and receive a signed response.

<a id="provable-question:ask-question"></a>
### Ask Question

type: **ask-question**

Sends a question message to the specified connection

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/question/0.1/question",
    "@id": <uuid>,
    "connectionId": <pairwise_did>
    "question": {
	  "notification_title": "Challenge Question",
	  "question_text": "Alice, are you on the phone with Bob from Faber Bank right now?",
	  "question_detail": "This is optional fine-print giving context to the question and its various answers.",
	  "valid_responses": [
	    {"text": "Yes, it is me", "nonce": "YES-<unique_id>"},
	    {"text": "No, that is not me!", "nonce": "NO-<unique_id>"}
	  ],
	  "@timing": {
	    "expires_time": "2019-05-15T00:06:04.748Z"
	  },
	  "external_links:": [
	    {"text": "Some external link", "src": 'https://www.externalwebsite.com/'},
	    {"src": "https://www.directlinkwithouttext.com/"},
	  ]
    }
}
```

#### Attributes
* `connectionId` the DID returned in the success status message of the connection protocol
* `question` contains:
    * `question_text`
    * `question_detail` **optional**
    * `valid_responses`
    * `@timing` **optional**
    * `external_links` **optional**

#### Notes
- See [this document](https://github.com/evernym/ConnectMe/blob/master/app/question/question-sender-usage.md) for more details on the question object and how it is interpreted.

<a id="provable-question:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the connection protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/question/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "user rejected the question",
        "code": 1
    },
    "timestamp": "2019-06-12 13:23:06Z"
}
```

<a id="provable-question:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/question/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 1,
    "message": <status message>,
    "content": "Yes, I'm trying to login"
}
```
#### Attributes

* `status` enum that resolves to one of 2 states:
    * `0` question has been sent
    * `1` question was answered. Response in content.
* `message` **optional** message relating to the status
* `content` **optional** content field associated with the status

#### Notes

* If the signature validation fails, a problem report will be sent and NOT a status message with status 1 and their response.

---

<a id="schema"></a>
## Schema

protocol: **schema** | ver: **0.1**

The *Schema* set of protocols encompass the necessary functionality to allow an enterprise to write a schema to the ledger.

<a id="schema:write"></a>
### Write Schema

type: **write**

Tells Verity to write a new schema to the ledger on behalf of the enterprise.

```json
{
	"@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/schema/0.1/write",
	"@id": <uuid>,
	"schema": {
		"name": <schema name>,
		"version": <schema version>,
		"attrNames": [
			"name",
			"age",
			...
		]
	}
}
```

#### Attributes

* `attrs` list of schema attributes

<a id="schema:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the schema protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/schema/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "",
        "code": 1024
    },
    "timestamp": "2019-06-12 13:23:06Z"
}
```

<a id="schema:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 0,
    "message": "Successfully wrote schema to ledger",
    "content": <schema_id>
}
```
#### Attributes

* `status` enum that resolves to one of 6 states:
    * `0` Successfully wrote schema to ledger (schema_id in content).
* `message` **optional** message relating to the status
* `content` **optional** content field associated with the status

---

<a id="cred-def"></a>
## Credential Definition

protocol: **cred_def** | ver: **0.1**

The *Credential Definition* set of protocols encompass the necessary functionality to allow an enterprise to write a credential to the ledger.

<a id="cred-def:write"></a>
### Write Credential Definition

type: **write**

Tells Verity to write a new credential definition to the ledger on behalf of the enterprise.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/write",
    "@id": <uuid>,
    "name": <name of cred def>,
    "schemaId": <schema_id>,
    "tag": <optional tag>,
    "revocation_details": {
        ... <optional revocation details>
    }
}
```

#### Attributes

* `schemaId` is the id of a schema already written to the ledger with the desired attributes

<a id="cred-def:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the connection protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "",
        "code": 1024
    },
    "timestamp": "2019-06-12 13:23:06Z"
}
```

<a id="cred-def:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/cred-def/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 0,
    "message": "Successfully wrote credential definition to ledger",
    "content": <cred_def_id>
}
```
#### Attributes

* `status` enum that resolves to one of 1 states:
    * `0` Successfully wrote credential definition to ledger (cred\_def\_id in content).
* `message` **optional** message relating to the status
* `content` **optional** content field associated with the status

---

<a id="credential"></a>
## Credential

protocol: **credential** | ver: **0.1**

The *Credential* set of protocols encompass the necessary functionality to allow an enterprise to issue a credential to an existing connection.

<a id="credential:credential"></a>
### Credential

type: **credential**

Send a credential to a user

```json
{
	"@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/credential",
	"@id": <uuid>,
	"connectionId": "<pairwise_did>",
	"credentialData":{
        "id": <uuid>,
        "credDefId": "did:sov:abcdefg12345",
        "credentialValues": {
            "name": "Joe Smith",
            "degree": "Bachelors",
            "gpa": "3.67"
        },
        "price": 3
    }
}
```

#### Attributes

* `connectionId` is the id of the connection to whom you want to send the credential
* `credentialData`
	* `id` unique UUID of the credential
	* `credDefId` Credential Definition ID of credential being sent
	* `credentialValues` key-value pairs of credential attribute fields with the specified params defined in the credential definition

<a id="credential:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the connection protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "",
        "code": 1024
    },
    "timestamp": "2019-06-12 13:23:06Z"
}
```

<a id="credential:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 3,
    "message": "Credential Accepted by User"
}
```
#### Attributes

* `status` enum that resolves to one of 4 states: 
    * `0` Offer sent to user
    * `1` Offer accepted by user
    * `2` Credential sent to user
    * `3` Credential accepted by user
* `message` **optional** message relating to the status

---

<a id="proof"></a>
## Proof

protocol: **proof** | ver: **0.1**

The *Proof* set of protocols encompass the necessary functionality to allow an enterprise to send a proof request to and receive a proof from an existing connection.

<a id="proof:request"></a>
### Proof Request

type: **request**

Send a proof request to a user

```json
{
	"@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/proof/0.1/request",
	"@id": <uuid>,
	"connectionId": "<pairwise_did>",
	"proofRequest":{
        "name": <proof name>,
        "proofAttrs": [
        	{name: 'name', restrictions: [{issuer_did: configJson['institution_did']}]},
    		{name: 'degree', restrictions: [{issuer_did: configJson['institution_did']}]}
        ],
        "revocationInterval": {}
    }
}
```

#### Attributes

* `connectionId` is the id of the connection to whom you want to send the credential
* `proof`
	* `name` The name of the proof request
	* `proofAttrs` 
		* `name` the name of the desired attribute
		* `restrictions` an array of objects representing any restrictions on the desired attribute

<a id="proof:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the proof protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/proof/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "",
        "code": 1024
    },
    "timestamp": "2019-06-12 13:23:06Z"
}
```

<a id="proof:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/proof/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 1,
    "message": "Proof received",
    "content": <proof attributes>
}
```
#### Attributes

* `status` enum that resolves to one of 2 states:
	 * `0` Proof request sent
    * `1` Proof received and validated (proof attributes in content)
* `message` **optional** message relating to the status

---


<a id="enroll"></a>
## Enroll

protocol: **enroll** | ver: **0.1**

The *Enroll* set of protocols encompass the necessary functionality to allow an enterprise to create a pairwise connection with an identity owner and quickly issue a specific credential.

<a id="enroll:new-enrollment"></a>
### New Enrollment

type: **new-enrollment**

Initiates the process to onboard a new user

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/enroll/0.1/new-enrollment",
    "@id": <uuid>,
    "connectionDetail":{
        "sourceId": "CONN_iAmAConnId",
        "phoneNo": "8001112222"
    },
    "credentialData":{
        "id": <uuid>,
        "credDefId": "did:sov:abcdefg12345",
        "credentialValues": {
            "name": "Joe Smith",
            "degree": "Bachelors",
            "gpa": "3.67"
        },
        "price": 3
    } 
}
```

#### Attributes

* `connectionDetail` contains:
    * `id` unique identifier for the connection, mapping to the enterprise connection
    * `phoneNo` **optional** *string* connection sms
* `credentialData` defines the credential details. Contains:
    * `id` unique identifier for the credential
    * `credDefId` resolver for a credential definition
    * `credentialValues` key-value pairs of credential attribute fields with the specified params defined in the credential definition
    * `price` **optional** price for credential

#### Notes

1) Connection and credential can be queried
2) If Connection already exists then this will just send the specified credential to the existing Connection.
3) Connection will be usable (unless deleted?) for sending other credentials and requesting proof.
4) DID comm is asynchronous so status updates will be sent in the response.

<a id="enroll:problem-report"></a>
### Problem Report

type: **problem-report**

Problem report for the enroll protocol. See [Problem report common](#Attributes) for details on optional attributes.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/enroll/0.1/problem-report",
    "@id": <uuid>,
    "~thread": {
        "pthid": <pthid>
    },
    "comment": {
        "en": "user rejected the connection request",
        "code": 1024
    },
    "who_retries": 3
    "timestamp": 2019-06-12 13:23:06Z
}
```

<a id="enroll:status"></a>
### Status

type: **status**

Verity generated message that gives human readable indications of the current status of an ongoing, complete, or cancelled enrollment.

```json
{
    "@type": "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/enroll/0.1/status",
    "@id": <uuid>
    "~thread": {
        "thid": <thid>,
        "seqnum": 3
    },
    "status": 5,
    "message": "process complete"
}
```

#### Attributes

* `status` enum that resolves to one of 6 states:
    * `0` invite has been sent
    * `1` invite was accepted by the user
    * `2` the offer was sent to the user
    * `3` the offer was received 
    * `4` the credential was sent
    * `5` the credential was accepted
* `message` **optional** message relating to the status

