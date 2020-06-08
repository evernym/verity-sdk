FROM ubuntu:16.04
WORKDIR /root

#ENV LIBVCX_VERSION 0.3.52800629-ee57591
ENV LIBINDY_VERSION 1.10.1
ENV VERITY_APPLICATION_VERSION 0.4.91638373.469f198

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
    add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial stable"

# Setup apt for evernym repositories
RUN curl https://repo.corp.evernym.com/repo.corp.evenym.com-sig.key | apt-key add -
RUN add-apt-repository "deb https://repo.corp.evernym.com/deb evernym-agency-dev-ubuntu main"

# install verity-application, ignoring failed post-install script
#     libvcx=${LIBVCX_VERSION} \
RUN apt-get update && apt-get install -y \
    libindy=${LIBINDY_VERSION} \
    libnullpay=${LIBINDY_VERSION} \
    indy-cli=${LIBINDY_VERSION}

RUN apt-get update && apt-get install -y \
    verity-application=${VERITY_APPLICATION_VERSION} \
    ; exit 0

RUN rm -rf /etc/verity/verity-application/*
ADD configuration/ /etc/verity/verity-application/.

ADD scripts/entrypoint.sh scripts/entrypoint.sh
ENTRYPOINT ["./scripts/entrypoint.sh"]

EXPOSE 9000