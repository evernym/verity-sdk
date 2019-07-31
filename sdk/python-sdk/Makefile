pytest:
	pytest --workers auto

lint:
	python3 -m pylint --load-plugins pylint_quotes src test

build:
	python3 setup.py sdist bdist_wheel

upload:
	python3 -m twine upload dist/*
