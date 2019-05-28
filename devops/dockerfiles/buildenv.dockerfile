FROM ubuntu:16.04

RUN apt-get update && apt-get install -y \
    software-properties-common \
    apt-utils \
    apt-transport-https \
    default-jdk \
    wget \
    tar \
    curl

RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master" && \
    apt-get update && apt-get install -y libindy

RUN cd /opt/ && \
    wget http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz && \
    tar -xvzf apache-maven-3.3.9-bin.tar.gz && mv apache-maven-3.3.9 maven

ENV M2_HOME=/opt/maven
ENV PATH=${M2_HOME}/bin:${PATH}

# Install node.js and npm
RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && \
    apt-get install -y nodejs
RUN npm i -g typescript tslint
