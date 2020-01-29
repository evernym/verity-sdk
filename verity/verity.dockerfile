FROM buildenv
WORKDIR /root

RUN apt-get update && apt-get install -y \
    iproute2 \
    jq

# Install ngrok
RUN wget https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && cp ngrok /usr/local/bin/.

# install certificates for evernym
RUN mkdir -p /usr/local/share/ca-certificates
RUN curl -k https://repo.corp.evernym.com/ca.crt | tee /usr/local/share/ca-certificates/Evernym_Root_CA.crt
RUN update-ca-certificates

# Setup apt for evernym and sovrin repositories
RUN curl https://repo.corp.evernym.com/repo.corp.evenym.com-sig.key | apt-key add -
RUN echo 'deb https://repo.corp.evernym.com/deb evernym-agency-dev-ubuntu main' | tee /etc/apt/sources.list.d/agency-dev_repo.corp.evernym.com.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88
RUN add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master"
RUN add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial stable"
RUN apt-get update

# install verity-application, ignoring failed post-install script
RUN apt-get install -y verity-application; exit 0
RUN rm -rf /etc/verity/verity-application/*
ADD configuration/ /etc/verity/verity-application/.

# Install and setup dynamodb
ADD scripts/dynamodb scripts/dynamodb
RUN sh scripts/dynamodb/install.sh
RUN sh scripts/dynamodb/clean-setup.sh

ADD scripts/entrypoint.sh scripts/.
RUN chmod +x scripts/entrypoint.sh
ENTRYPOINT ["./scripts/entrypoint.sh"]

EXPOSE 9000