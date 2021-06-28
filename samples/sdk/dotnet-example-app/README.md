### .Net Example Application

An example application that demonstrate simple SSI use cases using the .Net Verity SDK.

See [Getting Started](../../../docs/getting-started/getting-started.md) guide for a guided tutorial that makes use of this example application.

### Prerequisites

Install the following items:
* `libindy` -- Install a stable version. Follow the instructions on the   
  [indy-sdk Github Project Page](https://github.com/hyperledger/indy-sdk#installing-the-sdk). For **Windows**, see the section below.
* `Ngrok` -- This is a temporary installation to facilitate early experimentation.
* `.Net Core 3.1` -- Follow the instructions on the [Microsoft website](https://docs.microsoft.com/en-us/dotnet/core/install)

### Libindy installation guide for Windows

Many Windows users might find the official Libindy installation guide insufficient, and in this section, we've provided a detailed installation guide for Windows machines.  
For installation of Libindy on Windows, follow these steps:
* Go to  https://repo.sovrin.org/windows/libindy/stable/ and download the **latest version** of Libindy (be aware that the latest version will be somewhere in the middle of the list! ).
* Unzip archives to the directory where you want to save the library (it is recommended to save it somewhere where it won't be accidentally deleted, e.g. make a directory in (C:)).
* Your working directory for libindy will look like this:
    - `include`
        -  `...`
    - `lib`
        - `indy.dll`
        - `libeay32md.dll`
        - `libsodium.dll`
        - `libzmq.dll`
        - `ssleay32md.dll`
* Open the **lib** directory, and copy a path to it
* Type Environment Variables in the search bar and press Enter
* Click on Environment Variables
* Double click on Path under the System Variables, and a new window will appear
* Click on New and paste a full path to the lib directory there
* Make sure that you've saved the changes

### Build the example application

> NOTE: If you're working on **Windows**, you must open the Command Prompt as Administrator

In a root directory of the application execute the following commands:
```sh  
dotnet restore
dotnet build --configuration release
```  

### Run the example application

Follow the steps in the [Getting Started](../../../docs/getting-started/getting-started.md) tutorial.
