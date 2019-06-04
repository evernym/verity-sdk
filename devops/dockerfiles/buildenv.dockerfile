FROM ubuntu:16.04

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
    git

# Install python3.6
RUN add-apt-repository ppa:jonathonf/python-3.6 && \
    apt-get update && apt-get install -y \
    python3.6 \
    python3-pip

# Install Sovrin Dependencies
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master" && \
    apt-get update && apt-get install -y libindy libvcx libnullpay

# Install node.js and npm
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && \
    apt-get install -y nodejs
RUN npm i -g typescript tslint

# Install python dependencies
RUN python3.6 -m pip install --upgrade pip
RUN python3.6 -m pip install requests==2.20.0 python3-indy==1.8.1 python3-wrapper-vcx==0.2.4
