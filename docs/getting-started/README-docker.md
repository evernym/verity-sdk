To quickly try out Verity SDKs create a Docker image with pre-installed requirements:

```sh
docker build -f getting-started.dockerfile -t verity-sdk ../.. 
```
Start the Docker container:
```sh
docker run -it --rm verity-sdk
```

* To start example app in Node.js SDK run following commands:
```sh
cd /sdk/nodejs-sdk
node example.js
```
* To start example app in Python SDK run following commands:
```sh
cd /sdk/python-sdk/example
python3 app.py
```
* To start example app in Java SDK run following commands:
```sh
cd /sdk/java-sdk/example
mvn exec:java
```

© 2013-2020, ALL RIGHTS RESERVED, EVERNYM INC.
