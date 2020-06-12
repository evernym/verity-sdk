##Migration Guide 

### `0.2.0-beta` to `0.3.0-beta`
#### Removed protocols
To better support the interoperable `Aries` protocols several `0.6` versions of the protocols have been dropped. These `0.6` versions were designed and implemented before the `Aries` community fully organized and standardized these protocols.
 
 These include:
 * `Provision` `0.6`
 * `Connecting` `0.6`
 * `IssueCredential` `0.6`
 * `PresentProof` `0.6`
 
Please move to use the following versions instead:

 * `Provision` `0.6` -> `Provision` `0.7` 
 * `Connecting` `0.6` -> `Connecting` `1.0`
 * `IssueCredential` `0.6` ->  `IssueCredential` `1.0`
 * `PresentProof` `0.6` -> `PresentProof` `1.0`

These protocols work largely the same as the dropped version. Please see the example app for more information about their use.


#### Relationship Protocol
The `relationship` protocol was included in the `verity-sdk` `0.2.0-beta` version but was not mentioned in this doc. Since `Connecting` `0.6` is being dropped, the `relationship` protocol deserves special mention.

The `connecting` `0.6` did two tasks at once. It provisioned relationship assets inside the `verity-application` and handled the exchange of `DIDs` between the parties of the relationship. But these tasks have been divided between two protocols in the `connecting` `1.0` and the `relationship` `1.0` protocol. One must first create a `relationship` and then exchange `DID Doc` information (including the invite). See the example app for more information about this process.


#### Token Provisioning
To protect the SAAS Verity service, the creation of Agents is protected by an authorization token. This token is required when using the `provision` `0.7` protocols pointed at the Evernym demo and public `Verity Application`. The token is a JSON string that is provided by Evernym and must be passed to the constructor of the `provision` `0.7` protocol.

It looks like: 

```
{
      "sponseeId":"myId",
      "sponsorId":"evernym-test-sponsorabc123",
      "nonce":"123",
      "timestamp":"2020-06-05T21:33:36.085Z",
      "sig":"ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==",
      "sponsorVerKey":"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"
}
```
> **NOTE:** The token is mostly self-verifiable and will be verified before sending it to the `Verity Application`.

### `0.1.0-beta` to `0.2.0-beta`
The `0.2.0-beta` added better support for multiple versions of a protocol. As such, the Verity-SDK API required the ability to distinguish between different versions. This required changes to the API. These changes are limited to the import statements and interfaces used.
  
####Python:
The python sdk is backwards compatible with the API provided by `0.1.0-beta`. As such, no changes are required but imports should be updated. All protocols provided by `0.1.0-beta` have been moved to 0.6 versions of their respective protocols. And an implicit link has been created for the non-version protocol class.   
 
 To upgrade, changing the protocol imports is all that is required. Change protocol imports to pull from the new `v0_6` module. 
 
 Example:
 
 `from verity_sdk.protocols.Connecting import Connecting`
 
 becomes
 
 `from verity_sdk.protocols.v0_6.Connecting import Connecting`
 
 (Notice the **`v0_6`** module in the new import)
 
 ####NodeJs:
 The NodeJs sdk is backwards compatible with the API provided by `0.1.0-beta`. As such, no changes are required but sdk calls should be updated. All protocols provided by `0.1.0-beta` have been moved to 0.6 versions of their respective protocols. And an implicit link has been created for the non-version protocol class.   
  
  To upgrade, change calls to protocols class to include the new `v0_6` module:
  
  Example:
  
  `new sdk.protocols.Connecting(null, uuidv4(), null, true)`
  
  becomes
  
  `new sdk.v0_6.protocols.Connecting(null, uuidv4(), null, true)`
  
  (Notice the **`v0_6`** module in the new call)
  
####Java
Because of Java's type system, there are breaking changes to the Verity-SDK. The Interfaces for protocols have been refactored and renamed. As such, a new additional import is required for the version-specific interface for all protocols.

To upgrade, rename the protocol interface to versioned interface and import this new interface:

Example:
  
  ```
  Connecting connecting = Connecting.v0_6(UUID.randomUUID().toString(), true);
  ```
  
  becomes
  
  ```
import com.evernym.verity.sdk.protocols.connecting.v0_6.ConnectingV0_6; 
...
sdf ConnectingV0_6 connecting = Connecting.v0_6(UUID.randomUUID().toString(), true);
  ```

Also, a few objects have been moved a common package since they are used by multiple versions of a given protocol. An import change is all the is required.

Example:

`import com.evernym.verity.sdk.protocols.presentproof.Attribute;`

becomes

`import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;`