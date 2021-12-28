### .Net Example Application

An example application that demonstrate simple SSI use cases using the .Net Verity SDK.

See [Getting Started](../../../docs/getting-started/getting-started.md) guide for a guided tutorial that makes use of this example application.

### Prerequisites

Install the following items:
* `libvdrtools` -- Installation instructions can be found [here](https://gitlab.com/evernym/verity/vdr-tools#installing). For **Windows**, see the section below.
* `Ngrok` -- This is a temporary installation to facilitate early experimentation.
* `.Net Core 3.1` -- Follow the instructions on the [Microsoft website](https://docs.microsoft.com/en-us/dotnet/core/install)

### Libvdrtools for Windows

We do not currently provide VDR Tools artifacts for Windows, but we will be bringing this support in the future. At the moment, we recommend running this sample app with Docker, rather than locally. Alternatively, you can use Linux virtual machine or Windows Subsystem for Linux. 

### Build the example application

> NOTE: If you're working on **Windows**, you must open the Command Prompt as Administrator

In a root directory of the application execute the following commands:
```sh  
dotnet restore
dotnet build --configuration release
```  

### Run the example application

Follow the steps in the [Getting Started](../../../docs/getting-started/getting-started.md) tutorial.
