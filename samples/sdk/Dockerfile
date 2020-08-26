FROM ubuntu:18.04

ENV LIBINDY_VERSION 1.15.0-bionic
ENV VERITY_SERVER https://vas.pps.evernym.com

RUN apt-get update && \
    apt-get install -y \
        apt-transport-https \
        build-essential \
        curl \
        default-jdk \
        iproute2 \
        jq \
        maven \
        python3 \
        python3-pip \
        software-properties-common \
        unzip \
        vim

# Setup apt for Sovrin repository
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb bionic stable"

# Install libindy library from Sovrin repo
RUN apt-get update && apt-get install -y \
    libindy=${LIBINDY_VERSION}

# Install Ngrok
RUN curl -O -s https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && \
    cp ngrok /usr/local/bin/.

# Install NodeJS
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y nodejs

# Copy SDKs into the container
COPY /samples/ /samples

# Install Python3 packages
WORKDIR /samples/sdk/python-example-app
RUN pip3 install -r requirements.txt

# Install Java packages
ENV JAVA_HOME /usr/lib/jvm/java-1.11.0-openjdk-amd64/
WORKDIR /samples/sdk/java-example-app
RUN mvn test
RUN mvn exec:help # pre-download plug-in dependencies

# Install npm packages
WORKDIR /samples/sdk/nodejs-example-app
RUN npm install 2>/dev/null
RUN npm rebuild 2>/dev/null

# Install static HTTP server to serve generated QR code
RUN npm install -g http-server

WORKDIR /samples/sdk
EXPOSE 4000

# Start Ngrok tunnel for webhook URL in docker entrypoint
ENTRYPOINT ["/samples/sdk/entrypoint.sh"]

