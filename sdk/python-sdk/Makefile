REPO=https://kraken.corp.evernym.com
VERSION=$(shell python3 verity_sdk/print_version.py)
FILE="dist/verity-sdk-${VERSION}.tar.gz"
FILENAME="verity-sdk_${VERSION}.tar.gz"

pytest:
	pytest --workers auto

lint:
	python3 -m pylint --load-plugins pylint_quotes verity_sdk test

doc-build:
	sphinx-apidoc -f -o doc/_source/ verity_sdk/
	sphinx-build -c doc/  doc/_source/ doc/_build/

build-artifacts:
	python3 setup.py sdist bdist_wheel

upload:
	make build-artifacts
	echo "${VERSION}"
	echo "${FILE}"
	echo "${FILENAME}"
	curl -if -u "$${KRK_USERNAME}:$${KRK_PASSWORD}" -X POST -F "file=@${FILE};filename=${FILENAME}" "${REPO}/repo/python/upload"

upload-to-pypi:
	make build-artifacts
	python3 -m twine upload dist/*

