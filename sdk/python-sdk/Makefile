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
	python3 -m twine upload --verbose --skip-existing dist/*
