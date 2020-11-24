### .Net Example Application

**This example doesn`t use VeritySDK from NuGet**

An example application that demonstrate simple SSI use-cases using the .Net Verity SDK.

See [Getting Started](../../../docs/getting-started/getting-started.md) guide for a guided tutorial that makes use of this example application.  

### Prerequisites
Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the 
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).
* `Ngrok` -- This is a temporary installation to facilitate early experimentation. 
* `.Net Core 3.1` -- Follow the instructions on the [Microsoft website](https://docs.microsoft.com/en-us/dotnet/core/install)

### Build the example application
```sh
dotnet restore
dotnet build --configuration release
```

### Run example application
```sh
dotnet run
```