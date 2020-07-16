
## Verity Demo App

To quickly try out the demo web app using docker-based setup follow these steps:
- Fill in values for VERITY_URL, DOMAIN_DID and X_API_KEY (you should have received these inputs from Evernym) into **.env** file. Properly filled **.env** file should look like this:
```sh
VERITY_URL=https://vas.pps.evernym.com
DOMAIN_DID=W1TWvjCTGzHGEgbzdh5U4b
X_API_KEY=AkdrCwUhNXiQi3zgwKw2KhR6muAX1Q18phP4cfuMtvq4:4cBQC9EsbMa9T96KA4noZwLJQuVcd6KBwaqFhRqZQKFWT45VEm3jbPCm8S6bqhwh3UKEKAPkHeLz9Gb1d1YE1dW
```
- Create a docker image with pre-installed requirements. Run this command from the current folder:
```sh
docker build -t demo-web-app .
```
Start a Docker container:
```sh
docker run -p 3000:3000 --env-file .env -it demo-web-app
```

Demo web app will be available on http://localhost:3000

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
