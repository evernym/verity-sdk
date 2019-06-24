init:
	if [ ! -d ".venv" ]; then 
		python3.6 -m venv .venv
	fi
	source .venv/bin/activate

install:
	make init
	pip install -r requirements.txt

lint:
	pylint --load-plugins pylint_quotes **/*.py