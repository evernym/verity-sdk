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
        'vdr-tools~=0.0.5',
        'requests~=2.25.1',
        'base58~=2.1.0'
    ],
    python_requires='~=3.6',
    long_description=long_description,
    long_description_content_type='text/markdown',
    packages=setuptools.find_packages(),
)

