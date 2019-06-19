# Integration tests

For now, only the java-sdk is supported.

Run:

```
docker-compose rm -sf # Force remove all running containers to make sure old containers aren't being reused.
docker-compose build
docker-compose run --rm --name integration-tests java-integration-tests
```

It takes a little while to get going, and in total takes about 5 minutes.
