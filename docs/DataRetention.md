# Data Retention on Verity

SSI protocols often carry private data in its payload (eg. sharing a Proof presentation that contains personal information or issuing a Credential offer). Verity makes it possible to  configure how long this private data that was transfered as part of the SSI protocol is kept, so that the data is automatically removed from the Verity platform once it is not needed anymore.

The configuration can be made per identity domain (DomainDID) but also per protocol in a domain (eg issue-credential, present-proof). There are two configuration options:
- "expire-after-terminal-state", boolean, specifies if data should be removed imediatelly after the protocol is reached the terminal state. Eg. Once the proof presentation is delivered to Verifier, that presentation can be removed. The consequence is that if status is requested later, it will contain only state, but not the data of the presentation.
- "expire-after-days", the maximum number of days (1-730) protocol data is kept. If "expire-after-terminal-state" is set to true, this would happen only if the protocol is not finished. Eg. Credential offer is sent, but the Holder never accepted the Credential offer. That protocol message that contains the Credential offer will be kept this many days.

Default Evernym policy is {"expire-after-days": 3 days,"expire-after-terminal-state": true}, which translates to protocol data is deleted as soon as the protocol is completed and after 3 days for protocols that haven't been completed. That policy is applied to all protocols unless overriden. Every client can override the default policy for its own protocols and also policy for a specific protocol if needed by sending a request to support@evernym.com.
