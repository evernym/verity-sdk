pytest:
	pytest --workers auto

lint:
	python3 -m pylint --load-plugins pylint_quotes verity_sdk test

build-artifacts:
	python3 setup.py sdist bdist_wheel

upload:
	make build-artifacts
	python3 -m twine upload dist/*
