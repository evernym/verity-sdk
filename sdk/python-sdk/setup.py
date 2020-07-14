import setuptools

from verity_sdk.version import VERSION

with open('README.md', 'r') as fh:
    long_description = fh.read()

setuptools.setup(
    name="verity-sdk",
    version=VERSION,  # see verity_sdk/version.py
    author="Evernym, Inc.",
    author_email="dev@evernym.com",
    description='The official Python SDK for Evernym\'s Verity',
    license="Apache-2.0",
    url="https://github.com/evernym/verity-sdk",
    install_requires=[
        'python3-indy~=1.15.0',
        'requests~=2.22',
        'base58~=2.0.0'
    ],
    python_requires='~=3.6',
    long_description=long_description,
    long_description_content_type='text/markdown',
    packages=setuptools.find_packages(),
)

