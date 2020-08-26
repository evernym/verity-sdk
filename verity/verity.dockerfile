FROM ubuntu:18.04
WORKDIR /root

ENV LIBINDY_VERSION 1.15.0-bionic
ENV VERITY_APPLICATION_VERSION 0.4.95258837.49f840f

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
    git \
    python3-pip \
    iproute2 \
    jq \
    unzip

# Set JAVA_HOME
ENV JAVA_HOME /usr/lib/jvm/java-1.11.0-openjdk-amd64/

# Install ngrok
RUN wget https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && cp ngrok /usr/local/bin/.

# Add Evernym Cert
RUN mkdir -p /usr/local/share/ca-certificates
ADD configuration/Evernym_Root_CA.crt /usr/local/share/ca-certificates/Evernym_Root_CA.crt
RUN update-ca-certificates

# Setup apt for Sovrin repository
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88 && \
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb bionic stable"

# Setup apt for evernym repositories
RUN curl https://repo.corp.evernym.com/repo.corp.evenym.com-sig.key | apt-key add -
RUN add-apt-repository "deb https://repo.corp.evernym.com/deb evernym-agency-dev-ubuntu main"

# install verity-application, ignoring failed post-install script
RUN apt-get update && apt-get install -y \
    libindy=${LIBINDY_VERSION} \
    libnullpay=${LIBINDY_VERSION} \
    indy-cli=${LIBINDY_VERSION}
RUN apt-get update && apt-get install -y \
    verity-application=${VERITY_APPLICATION_VERSION} \
    ; exit 0
RUN apt-get autoremove -y && \
    apt-get clean && \
    sed -i '/repo\.corp\.evernym\.com/d' /etc/apt/sources.list && \
    rm -rf /var/lib/apt/lists/* \
    ; exit 0

# Add LevelDB dependency
RUN mvn dependency:get \
    -Dartifact=org.iq80.leveldb:leveldb:0.9 \
    -Dartifact=org.fusesource.leveldbjni:leveldbjni-all:1.8

RUN rm -rf /etc/verity/verity-application/*
ADD configuration/ /etc/verity/verity-application/.

ADD scripts/entrypoint.sh scripts/entrypoint.sh
ENTRYPOINT ["./scripts/entrypoint.sh"]

EXPOSE 9000
