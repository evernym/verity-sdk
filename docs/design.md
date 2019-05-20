# Verity-SDK Design
The purpose of this and any subsequent documents is to provide an adequate overview of the Verity-SDK architecture, goals, and implementation details. 

## Overview
Verity-SDK is a lightweight stateless library to be provided to customers of the Verity Agent Service to make it simple for developers to interact with and consume the exposed protocols of the Verity-Agent.

## Architecture
### Stateless library
Verity-SDK is by design intended to be stateless thus giving control back to the consumer of the apis provided by the library. This choice leads to some very large implications for design of the library, namely it will function has a helper to library to allow ease of use with verity, but will *not* interact with Verity-Agent itself. 
