## Verity Sample Web App

This is a sample web app demonstrating how Verity REST API can be used for SSI flows like connection establishment, credential issuance, proof exchange or committed answer.

### Launching the application Locally

Requirements:
- You have received Domain DID and REST API key from Evernym
- You have NodeJs v12 installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/)).
> NOTE: Ngrok is used to create a public webhook URL which will forward response messages from Verity Application Server to the web app. If you have capabilities to start the application on a public IP address then you don't need ngrok]

To try out the application follow these steps:

- In a separate terminal window start ngrok for port 3000 and leave it running:
```sh
ngrok http 3000
```
- Install required NodeJs packages:
```sh
npm install
```
- Start the web app
```sh
node app.js
```

- Fill in prompts for input parameters:
```sh
Verity Application URL: https://vas.pps.evernym.com
Domain DID: <copy domain DID received from Evernym>
X-API-KEY: <copy API key received from Evernym>
Webhook URL: <copy public URL address of the Ngrok forwarding tunnel to your local port 3000>
```

The application will be available on http://localhost:3000

### Launching the application using Docker

Requirements:
- You have received Domain DID and REST API key from Evernym
- You have Docker installed

To start the app follow these steps:
- Fill in values for VERITY_URL, DOMAIN_DID and X_API_KEY (you should have received these inputs from Evernym) into **.env** file in the current folder. A properly filled **.env** file should have this format:
```sh
VERITY_URL=https://vas.pps.evernym.com
DOMAIN_DID=W1TWvjCTGzHGEgbzdh5U4b
X_API_KEY=AkdrCwUhNXiQi3zgwKw2KhR6muAX1Q18phP4cfuMtvq4:4cBQC9EsbMa9T96KA4noZwLJQuVcd6KBwaqFhRqZQKFWT45VEm3jbPCm8S6bqhwh3UKEKAPkHeLz9Gb1d1YE1dW
```
> NOTE: These are just sample reference values. These values will NOT work if left unchanged. You should specify DOMAIN_DID and X_API_KEY that you received from Evernym
- Create a docker image with pre-installed requirements. Run this command from the current folder:
```sh
docker build -t demo-web-app .
```
Start a Docker container:
```sh
docker run -p 3000:3000 --env-file .env -it demo-web-app
```

The application will be available on http://localhost:3000

### Suggestions?

Are there other sample apps you would like to see here? [Send us your suggestions!](mailto:support@evernym.com)

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.