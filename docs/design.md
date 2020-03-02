# Verity SDK Design
The purpose of these documents is to provide an adequate overview of the Verity SDK architecture, goals, and implementation details. 

## Overview
Verity SDK is a lightweight stateless library that is provided to customers of the Verity Agent Service to make it simple for developers to interact with and consume the exposed protocols of the Verity Agent.

## Architecture
By design, Verity SDK is stateless, which gives control of the library-provided APIs back to the consumer. This architecture has significant implications for the design of the library: namely, it will function as a helper to the library for ease of use with Verity, but it will not interact with the Verity Agent itself. 
