# .NET Verity SDK

This is the .Net SDK for Evernym's Verity application.

Instructions for setting up your .NET development environment to use the Verity SDK.

## Prerequisites

Install the following items:

* `.NET Core 3.1` -- Install .Net Core 3.1. Follow the instructions on the
[Microsoft website](https://docs.microsoft.com/en-us/dotnet/core/install/).

* `libindy` -- Install a stable version. Follow the instructions on the
[indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk).

* `Mono` -- Install Mono (if you are using Ubuntu or Mac). Follow the instructions on the
[Mono website](https://www.mono-project.com/download/stable/).


## How to build

### Ubuntu based distributions (Ubuntu 18.04)

Open terminal at location "\verity-sdk\sdk\dotnet-sdk"

#Restore nuget-packages for project:

```sh
dotnet restore
```

#Build SDK:

```sh
dotnet build --configuration release
```	
or
```sh
dotnet publish --configuration release
```
	
Compiled files will be placed in "bin\release\netstandart2.0"

#Build nuget-package:

```sh
dotnet pack --configuration release
```

Compiled files will be placed in "bin\release\Evernym.Verity.SDK.{version}.nupkg"

#Run tests:

```sh
dotnet test --logger "console;verbosity=detailed"
```

### Windows

Simply build the VeritySDK.sln file using Visual Studio, msbuild, dotnet or whatever build system your .NET implementation and platform supports.
The unit-tests can be run using the 'dotnet test' command or by using Test Explorer in Visual Studio.
The project also includes a NuGet package definition which can be built using the 'dotnet pack' command or by choosing publish on the project in Visual Studio.


### Using the SDK 

The .NET SDK can be used in any .NET project by referencing the NuGet package which can be built using the instructions above or obtained from the
[nuget.org](https://www.nuget.org/packages/Evernym.Verity.SDK) package repository.
