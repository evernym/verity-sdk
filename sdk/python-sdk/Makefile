REPO=https://kraken.corp.evernym.com
VERSION=$(shell python3 verity_sdk/version.py)
FILE="dist/verity-sdk-${VERSION}.tar.gz"
FILENAME="verity-sdk_${VERSION}.tar.gz"

pytest:
	pytest --workers auto

lint:
	python3 -m pylint --load-plugins pylint_quotes verity_sdk test

build-artifacts:
	python3 setup.py sdist bdist_wheel

upload:
	make build-artifacts
	curl -if -u "$${KRK_USERNAME}:$${KRK_PASSWORD}" -X POST -F "file=@${FILE};filename=${FILENAME}" "${REPO}/repo/python/upload"