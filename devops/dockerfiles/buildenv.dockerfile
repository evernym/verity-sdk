FROM ubuntu:18.04

RUN apt-get update && apt-get install -y \
    software-properties-common \
    apt-utils \
    apt-transport-https \
    default-jdk \
    wget \
    tar \
    curl \
    python-dev \
    maven \
    openssh-client \
    git \
    plantuml \
    python3-pip

# Install Sovrin Dependencies
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master" && \
    apt-get update && apt-get install -y libindy=1.9.0~1122 libvcx=0.3.0~1122 libnullpay=1.9.0~1122

# Install node.js and npm
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y nodejs
RUN npm i -g typescript tslint

# Install python dependencies
RUN python3 -m pip install requests==2.21.0 python3-indy==1.9.0 python3-wrapper-vcx==0.3.0 pylint==2.3.1 pylint-quotes==0.2.1 aiohttp==3.5.4 pytest-mock==1.11.2 twine

# Set JAVA_HOME
ENV JAVA_HOME /usr/lib/jvm/java-1.11.0-openjdk-amd64/
