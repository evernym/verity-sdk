# Using Out-of-Band protocol to identify a returning user

A common pattern we have seen in the early days of Aries agents is a user with a browser getting to a point where a connection is needed between the website's (enterprise) agent and the user's mobile agent. A QR invitation is displayed, scanned and a protocol is executed to establish a connection. Life is good!

However, with the invitation processes described in the [Aries Connections protocol](https://github.com/hyperledger/aries-rfcs/tree/master/features/0160-connection-protocol), when the same user returns to the same page, the same process is executed (QR code, scan, etc.) and a new connection is created between the two agents. There is no way for the user's agent to say "Hey, I've already got a connection with you. Let's use that one!"

Out-of-band invitations described in the [Aries Out-of-Band protocol](https://github.com/hyperledger/aries-rfcs/tree/master/features/0434-outofband) aim to solve the returning user problem.

When the user already has a connection with the Enterprise agent website, scanning a second QR code (containing a new Out-of-band invitation) will not create a new connection. Instead, a user agent will respond with the reuse message over the first connection referencing the 2nd invitation. This way the enterprise agent can identify a returning user (and log the user in). The enterprise agent will use the first connection for future communication with the user in the current session.

A sample application demonstrating a returning user journey with Out-of-band invitations can be found [here](../../samples/rest-api/out-of-band)