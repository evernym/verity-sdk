## Verity Demo App

This is a sample web app which is using Verity REST API.

Requirements:
- You have received Domain DID and REST API key from Evernym
- You have NodeJS v12 installed
- You have ngrok installed ([https://ngrok.com/](https://ngrok.com/)). [Ngrok is used to create a public webhook URL which will forward response messages from Verity Application Server to the demo web app. If you have capabilities to start the application on a public IP address then you don't need ngrok]

To try out the  demo web app follow these steps:

- In a separate terminal window start ngrok for port 3000 and leave it running :
```sh
ngrok http 3000
```
- Install required NodeJS packages:
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
Webhook URL: <copy public URL address of ngrok forwarding tunnel for your local port 3000>
```

Demo web app will be available on http://localhost:3000
