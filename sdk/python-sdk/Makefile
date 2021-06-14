REPO=https://kraken.corp.evernym.com
VERSION=$(shell python3 setup.py -q --version 2> /dev/null)
FILE="dist/verity-sdk-${VERSION}.tar.gz"
FILENAME="verity-sdk_${VERSION}.tar.gz"

pytest:
	pytest --workers auto

lint:
	python3 -m pylint --load-plugins pylint_quotes verity_sdk test

doc-build:
	pdoc --force --html --output-dir doc/_build verity_sdk
	mv --force doc/_build/verity_sdk/* doc/_build/
	rm -rf doc/_build/verity_sdk

build-artifacts:
	python3 setup.py sdist bdist_wheel

upload:
	make build-artifacts
	python3 -m twine upload --verbose --repository-url $TWINE_REPO_URL dist/*

upload-to-pypi:
	make build-artifacts
	python3 -m twine upload --skip-existing dist/*

