
# Getting Started with the Verity SDK for Java
## Standing Up a Toy Verity Application
To use the verity-sdk, we will need an instance of the Verity Application to interact with. 

We have two means of standing up an instance of the Verity Application:
1. Build and/or run a limited instance of Verity Application in a docker container.
1. Have a member of our Customer Success team provision a small instance of the Verity Application in the cloud.

### Docker
#### Build
Building the Verity Application docker image assumes access to:
1. Internal Evernym Dev Debian repo
1. Internal Evernym Debian public key
1. Sovrin public Debian repo
1. Ubuntu public mirrors

At the root of this repo run this cmd to build the image:
 
```docker build -t verity -f verity/verity.dockerfile verity/```

This should build a new image tagged with

`verity:latest`

#### Run


Docker is very flexible and run many ways. The follow is an example of a `docker` run command to run Verity Application.

```docker run --name verity -it --rm -p 9000:9000 -e VERITY_SEED=NjRkNmM1NzUzMzlmM2YxYjUzMGI4MTZl -e NETWORK=team1  verity```

* `--name verity` - gives the running docker instance the name `verity`
* `-it`  - helps with stdout in the console where it is being run
* `--rm` - will remove the container after exiting
* `-p 9000:9000` - exposes the Verity Application port
* `-e` - enviroment variables
* `verity` - points to the image create previously

**Enviroment Variables**
* VERITY_SEED - Seeds the DID and Verkey used for the verity endpoint
* NETWORK - Selects the Identity Network used by Verity Application
  * team1 - points to the team1 ledger
  * demo - points to the demo ledger
  
The running container uses a script that will guild the user and display details about the running Verity Application.

The Verity Application requires a public DID is on used Identity Network. The script will pause and allow for the User to put the generated DID and its verkey onto the ledger. 

Logs from the Verity Application can be displayed pressing enter when requested after the application has finished starting up.

### Cloud

See your friendly neighborhood Customer Sucess team member for a cloud-based Verity Application.

**TODO** Add information for this step

## Running the Java Example App
The Java Example App is a simple showcase of the Java Verity-SDK.

Building the Java Example App requires the follow:
1. A recent version of the Maven build tool.
1. Internal Evernym Dev Maven repo
1. Public Sovrin Maven repo
1. Public Maven Centrol repo

### Build

1. Move the Example App project folder
  
   `cd verity-sdk/sdk/java-sdk/example`
2. Compile the Example App (using)

   `mvn compile`
   
### Run
The Example App simulates a simple Credential example work flow. This sample interaction requires the use of Connect.me mobile app. The mobile app must be on the same Identity Network as the Verity Application.

**TODO** Add instructions for setup of connect.me 

#### Launch
`mvn exec:java` will star the Example App and start the sample interaction.

#### Interactions
The Example App guides the User through a series of interactions as explain below:

1. Provisioning -
   
   sdf
1. Connecting
1. Ask a Committed Answer
1. Write Schema to Ledger
1. Write Credential Definition to Ledger
1. Issue Degree Credential
1. Request Proof Presentation

<!--## Running the Example Java Application (Ubuntu 16.04)

These steps target the x86-64 architecture running Ubuntu 16.04. If you want to run this inside a fresh VM, install [vagrant](https://www.vagrantup.com/) and in an empty folder run:

```sh
vagrant init ubuntu/xenial64
vagrant up
vagrant ssh
```

1. You will need docker, Java 8 (JDK), Maven, libindy and python3-pip installed on your system

	```sh
	sudo apt-get update
	sudo apt-get install -y docker.io default-jdk maven python3-pip
	sudo apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88
	sudo add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master"
	sudo apt-get update
	sudo apt-get install -y libindy=1.9.0~1122
	```
	
2. You will need to be a Trust Anchor on the Sovrin Test Ledger to run this example. Use the `tools/new_did.py` script **in this repository** to create a new DID and then email the DID and VerKey to [support@sovrin.org](mailto:support@sovrin.org) asking for that DID to be written to the Staging Net. You should received a response within 12 hours on a weekday (often much sooner!).

	You will also need `tools/requirements.txt` from this repository. It's probably best to clone this repository and keep it handy.

	```
	git clone https://github.com/evernym/Getting-Started-With-The-Verity-SDK
	cd Getting-Started-With-The-Verity-SDK
	pip3 install -r tools/requirements.txt --user
	./tools/new_did.py
	```

	:exclamation: Don't send the seed! Keep it safe. You will need it to start the Mock Verity application and any other time you want to write to the Test Ledger.

3. One directory back from this repo (not inside the Getting-Started-With-The-Verity-SDK folder) create a new Java application with [maven](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).
	
	```sh
	mvn archetype:generate \
		-DgroupId=com.mycompany.app \
		-DartifactId=example-app \
		-DarchetypeArtifactId=maven-archetype-quickstart \
		-DarchetypeVersion=1.4 \
		-DinteractiveMode=false
	cd example-app
	```

4. Copy `java/pom.xml` from this repository to the `pom.xml` of your application

        ```sh
        cp ../Getting-Started-With-The-Verity-SDK/java/pom.xml pom.xml
        ```

	This file is similar to the pom.xml file generated by maven, but it adds the Verity SDK JAR file as a dependency to your project and increases the target Java version to 1.8.
	
5. Copy `java/App.java` and `java/Listener.java` from this repository to `src/main/java/com/mycompany/app/.` of your example application.

        ```sh
        cp ../Getting-Started-With-The-Verity-SDK/java/*.java src/main/java/com/mycompany/app/.
        ```

6. Start the Mock Verity service locally with your Trust Anchor Seed created in step 2.

	```sh
	sudo docker pull evernymdev/mock-verity # Get latest version
	sudo docker run -itd --rm --network host evernymdev/mock-verity <YOUR_TRUST_ANCHOR_SEED>
	```

7. Provision against Mock Verity

	This is the process whereby your application registers and exchanges keys with the Verity application. You will need `tools/provision_sdk.py` from this repository. Also, make sure to replace \<WALLET\_KEY\_HERE\> with a new wallet password.

	```sh
        python3 ../Getting-Started-With-The-Verity-SDK/tools/provision_sdk.py --wallet-name verity-sdk-test-wallet http://localhost:8080 <WALLET_KEY_HERE> > verityConfig.json
	```
	
	As shown in the above command, the resulting config is copied to `example-app/verityConfig.json`
	
	Note: This wallet is only used to store the keys you use to communicate with Verity.
	
	:exclamation: If you restart Mock Verity at any point, you will need to provision again.


8. Build and run the example application.

	From inside the `example-app` directory:

	```sh
	mvn package
	mvn exec:java -Dexec.mainClass="com.mycompany.app.App"
	```
	
9. The logs for the example application will print something like this:
	
	```
	Invite Details: {"sc":"MS-101","threadId":null,"s"...
	```
	
	Copy the invite details JSON object into any QR code generator.  [This one](https://www.qr-code-generator.com/) works well, but you will need to click on the "Text" tab. 

10. Download [Connect.Me](https://connect.me/) from the [App Store](https://itunes.apple.com/us/app/connect-me/id1260651672?mt=8) or the [Google Play Store](https://play.google.com/store/apps/details?id=me.connect&hl=en).

11. When you setup the app, make sure to check the "Use Staging Net" option at the bottom of the screen or this demo won't work.

	![Connect.Me Developer Mode Switch](https://i.postimg.cc/pTrdMszg/IMG-0116.png)

12. Scan the QR code with Connect.Me. You will then receive a Connection Invite, Challenge Question, Credential Offer, Credential and Proof Request in that order, demonstrating all of the protocols currently supported by the Verity SDK. -->

© 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.

