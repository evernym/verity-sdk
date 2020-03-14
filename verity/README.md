# Verity in Docker

## Build

```
docker build -t verity --network host -f verity/verity.dockerfile verity/
```

The `--network host` is required only when using `sshuttle-helper`.

## Run

```
docker run --name verity -it --rm --network host verity -s 000000000000000000000000Trustee1 -e team1
```

### Help Menu

```
usage: ./entrypoint.sh -s|--enterprise-seed ENTERPRISE_SEED -e|--environment ENVIRONMENT

positional arguments:
	 -s, --enterprise-seed	(REQUIRED) seed for Verity's primary did and verkey
	 -e, --environment	 one of "demo" or "team1". default: "demo"
```

The DID associated with `enterprise-seed` must already be written to the ledger.

© 2013&#8211;2020, ALL RIGHTS RESERVED, EVERNYM INC.