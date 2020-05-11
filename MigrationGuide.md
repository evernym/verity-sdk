##Mirgration Guide 

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