# Data Retention on Verity

SSI protocols often carry private data in their payloads (eg. sharing a proof presentation that contains personal information or issuing a credential offer). Verity makes it possible to configure how long to keep the private data that was transferred as part of the SSI protocol, so that the data is automatically removed from the Verity platform once it is not needed anymore.

The configuration can be made per a tenant's identity domain (DomainDID) but also per protocol in a domain (eg issue-credential, present-proof). There are two configuration options:
- "expire-after-terminal-state", boolean, specifies if data should be removed immediately after the protocol has reached the terminal state. Example: Once the proof presentation is delivered to the Verifier, that presentation can be removed. The consequence is that if status is requested later, it will contain only state, but not the data of the presentation.
- "expire-after-days", the maximum number of days (1-730) that the protocol data will be kept. If "expire-after-terminal-state" is set to true, this would happen only if the protocol is not finished. Example: If the credential offer is sent, but the Holder never accepted the credential offer, that protocol message that contains the credential offer will be kept this many days.

The default Evernym policy is {"expire-after-days": 3 days,"expire-after-terminal-state": true}, which translates to protocol data is deleted as soon as the protocol is completed and after 3 days for protocols that haven't been completed. That policy is applied to all protocols unless overridden.

If needed, every tenant can override the default policy for its own protocols and also the policy for a specific protocol by sending a request to support@evernym.com.
