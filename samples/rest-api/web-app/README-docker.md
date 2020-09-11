## Verity Sample Web App

To quickly try out the demo web app using docker-based setup follow these steps:
- Fill in values for VERITY_URL, DOMAIN_DID and X_API_KEY (you should have received these inputs from Evernym) into **.env** file. A properly filled **.env** file should have this format:
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

Demo web app will be available on http://localhost:3000

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
