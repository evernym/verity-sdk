# How to Handle Asynchronous Responses in Your Client Application

## General Flow
Verity’s APIs are inherently asynchronous, meaning that you initiate actions by calling the APIs, and then find out about their completion at a later point in time. Actions can’t complete reliably during a single HTTP request because the other side of the request often involves business processes or actions by humans; examples include accepting invitations or offers and approving actions. The actions of these entities are unpredictable. The business process could include any number of steps and humans are not guaranteed to be connected to the Internet or paying attention when you make the API call that initiates the interaction with them. Timeouts are a useful failsafe, but they don’t help in a fundamental way.

Asynchronous APIs follow a predictable pattern: make an initial call that returns a handle; later, check back to find out the status of the action by referencing its handle. Alternatively (and sometimes more conveniently), you can specify a webhook that will be invoked on your side when your request has a status update. Either way, the scalability and performance of the system can be drastically better than a blocking API. For this to work, you are required to remember the actions that you have in process so the webhook or the polling that you do can connect a particular bit of progress back to the work you began. If your system is small, you could create a blocking thread for each pending callback, but this is suboptimal because it ties up resources on your side. A better pattern is to record your unsettled work, either in a lookup data structure in RAM or in a persistent DB. When you receive news of progress, you can look up the previous work, remember what it means, and take appropriate action to refresh a UI or trigger further reactions on your side.

A typical flow is as follows:
* Register webhook endpoint where you want to receive responses from Verity
* Submit a request to Verity (by sending HTTP POST request)
* Receive a `202 Accepted` response which indicates that the work is pending (it does not mean the work is done).
* The response includes a thread ID which you persist along with the other state associated with the request.
* Move on to other work.
* When the webhook is triggered, look up the thread ID of the response to get the previous state.
* Merge that state with the additional information in the webhook response payload.
* Proceed with your protocol.

## Message Queue and Webhooks 
The Verity Application Server has an internal message queue that is proprietary. When the VAS receives the request, it will perform the functions necessary to process that request and send a message back to a webhook server that you have created to handle the callbacks. The webhook receives the message and then determines the next step, which sometimes will be a second call to the VAS to process the former step. Often credential exchanges and proofs require a multiple-step process, which is why the service is asynchronous. Credential Offers and Proofs generally remain in the Queue until they are rejected or accepted by the individual using the end wallet app (most likely a mobile device).

A typical Issuer flow is as follows:
* An Issuer DID is set up on the VAS.
* A Webhook is set up and established on the VAS to receive messages for the Issuer DID.
* Credential definitions are written to the Ledger for the DID.
* A Credential Offer is generated and sent to an end user (either through OOB or with a Relationship established).
* Credential Offer is processed and sent to the Relationship (and sent to the queue as a pending request).
* The recipient accepts the offer (or rejects it).
* The credential data is sent to the recipient (or the offer is removed from the queue in the event that they reject the offer). 

## Background Reading
The Aries RFCs describe how this approach to asynchronous programming should work.

From [RFC 0011 Decorators](https://github.com/hyperledger/aries-rfcs/blob/18c4f82990e83e82bd01551deb97206533f081da/concepts/0011-decorators/README.md)

> What we want instead is a way to inject into any message the idea of a thread, such that we can easily associate responses with requests, errors with the messages that triggered them, and child interactions that branch off of the main one. This is the subject of the message threading RFC, and the solution is the ~thread decorator, which can be added to any response

From [RFC 0008: Message ID and Threading](https://github.com/hyperledger/aries-rfcs/blob/18c4f82990e83e82bd01551deb97206533f081da/concepts/0008-message-id-and-threading/README.md)

> Because multiple interactions can happen simultaneously, it's important to differentiate between them. This is done with a Thread ID or thid.
> The Thread ID is the Message ID (@id) of the first message in the thread. The first message may or may not declare the ~thread attribute block; it does not, but carries an implicit thid of its own @id.

## Example in Verity
This example in Verity can illustrate the flow. The example uses the Verity 1.0 protocol that implements Aries Interop Profile 1.0.

Using Verity SDK to create a relationship protocol instance, results in a `a34f4d00-5bcb-4ad6-b368-b8b634d6efbf` `thid` and the following response on the webhook:

    {
    	"@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/created",
    	"~thread": {
    		"thid": "a34f4d00-5bcb-4ad6-b368-b8b634d6efbf"
    	},
    	"@id": "b6c0f7ff-7fd4-4d00-9241-c3a0b47c86c0",
    	"verKey": "2V6p8DpC7Er4cy74p00000000000000000000x4NLj9u",
    	"did": "3iyPT00000000001ymd1Sh"
    }

Using the `thid` from this response to generate a connection invitation using Verity SDK results in the following response on the webhook:

    {
    	"inviteURL": "http://192.168.86.54:8083/agency/msg?c_i=eyJsYWJlbCI6Imlzc3VlciIsInNlcnZpY2VFbmRwb2ludCI6Imh0dHA6Ly8xOTIuMTY4Ljg2LjU0OjgwODMvYWdlbmN5L21zZyIsInJlY2lwaWVudEtleXMiOlsiMlY2cDhEcEM3RXI0Y3k3NHBlRldWZjVzQ1NwZFdQWHZ0OEI5Y3g0TkxqOXUiXSwicm91dGluZ0tleXMiOlsiMlY2cDhEcEM3RXI0Y3k3NHBlRldWZjVzQ1NwZFdQWHZ0OEI5Y3g0TkxqOXUiLCJFVExnWktlUUVLeEJXN2dYQTZGQm43bkJ3WWhYRm9vZ1pMQ0NuNUVlUlNRViJdLCJAdHlwZSI6ImRpZDpzb3Y6QnpDYnNOWWhNcmpIaXFaRFRVQVNIZztzcGVjL2Nvbm5lY3Rpb25zLzEuMC9pbnZpdGF0aW9uIiwiQGlkIjoiOTk4YzU3ZTctOGZlMC00YmZhLTgxZGQtNjZkZjY2ZTU2YzdkIn0=",
    	"@type": "did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation",
    	"~thread": {
    		"thid": "a34f4d00-5bcb-4ad6-b368-b8b634d6efbf"
    	},
    	"@id": "231b1426-1526-4da9-991d-77d5122db453"
    } 

Which, as expected, contains the same `thid`.

This is great, because the webhook can store/index responses by this `thid`, and your code can retrieve messages from the webhook using this `thid`.

At this point the relationship protocol is complete.

The `inviteUrl` contains the following decoded connections invitation, which is used by the holder to start the connections protocol. Notice that there is no `~thread` decorator and the `@id` is not the `thid` used in the relationship protocol. As expected, it is a new `@id` for a new protocol and no `~thread` decorator is present, because this is effectively the first message in the connections protocol.

    {
    	"recipientKeys": ["2V6p8DpC7Er4cy74peFWVf5sCSpdWPXvt8B9cx4NLj9u"],
    	"@type": "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/invitation",
    	"label": "issuer",
    	"serviceEndpoint": "http://192.168.86.54:8083/agency/msg",
    	"routingKeys": ["2V6p8DpC7Er4cy74p00000000000000000000x4NLj9u", "ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV"],
    	"@id": "998c57e7-8fe0-4bfa-81dd-66df66e56c7d"
    }

The ConnectMe app or the LibVCX in the Mobile SDK can process this invitation in `vcxCreateConnectionWithInvite` or `vcxConnectionConnect`. This would begin the connections protocol by the holder.

The issuer would then get the request to connect from the holder and send a `did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received` to the webhook.

The issuer responds to the holder and sends `did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent` to the webhook.

Both the `request-received` and `response-sent` messages sent to the webhook have the same `thid`, but do not have a `pthid` and, as expected, the `thid` does not match the `@id` from the invite. Because there is no `pthid` in the `request-received` and `response-sent messages`, they cannot be linked to the original `@id` included in the `inviteDetails`.