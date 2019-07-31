import setuptools

with open("README.md", "r") as fh:
    long_description = fh.read()

setuptools.setup(
    name="verity-sdk",
    version="0.0.2",
    author="Evernym, Inc.",
    author_email="spencer.holman@evernym.com",
    description="The official Python SDK for Evernym's Verity",
    long_description=long_description,
    long_description_content_type="text/markdown",
    packages=setuptools.find_packages(),
)
