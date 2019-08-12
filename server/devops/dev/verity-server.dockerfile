FROM ubuntu:16.04

# Adding packages to run curl (including ssl) 
RUN apt-get update -y && apt-get install -y ca-certificates curl software-properties-common

# Adding evernym ca cert
RUN mkdir -p /usr/local/share/ca-certificates
RUN curl -k https://repo.corp.evernym.com/ca.crt | tee /usr/local/share/ca-certificates/Evernym_Root_CA.crt
RUN update-ca-certificates

RUN curl -sL https://deb.nodesource.com/setup_8.x | bash - && apt-get install -y nodejs
RUN npm i -g npm@latest

WORKDIR /root

RUN apt-get update -y && apt-get install -y gcc \
  pkg-config \
  curl \
  build-essential \
  libsodium-dev \
  libssl-dev \
  libgmp3-dev \
  libsqlite3-dev \
  cmake \
  debhelper \
  apt-transport-https \
  ca-certificates \
  software-properties-common \
  wget \
  curl \
  libpng16-16 \
  libcairo2-dev \
  libjpeg8-dev \
  libpango1.0-dev \
  libgif-dev \
  g++ \
  unzip \
  git \
  htop \
  nano \
  vim \
  fakeroot \
  jq \
  devscripts \
  debhelper

# install certificates for evernym
RUN mkdir -p /usr/local/share/ca-certificates
RUN curl -k https://repo.corp.evernym.com/ca.crt | tee /usr/local/share/ca-certificates/Evernym_Root_CA.crt
RUN curl https://repo.corp.evernym.com/repo.corp.evenym.com-sig.key | apt-key add -
RUN echo 'deb https://repo.corp.evernym.com/deb evernym-agency-dev-ubuntu main' | tee /etc/apt/sources.list.d/agency-dev_repo.corp.evernym.com.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys 68DB5E88
RUN add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial master"
RUN add-apt-repository "deb https://repo.sovrin.org/sdk/deb xenial stable"
RUN update-ca-certificates

RUN mkdir -p /var/lib/verity-server/
RUN mkdir -p /etc/verity-server/
ADD devops/dev/get-deps.sh /var/lib/verity-server/get-deps.sh
ADD devops/dev/get-sdk-version.sh /var/lib/verity-server/get-sdk-version.sh
ADD package.json /var/lib/verity-server/package.json
ADD package-lock.json /var/lib/verity-server/package-lock.json

WORKDIR /var/lib/verity-server/

ARG libnullpay_pkg_name=libnullpay_1.8.1_amd64.deb

RUN apt-get update -y && apt-get install -y $(./get-deps.sh libvcx=$(./get-sdk-version.sh))

RUN wget -P /usr/lib https://repo.sovrin.org/sdk/lib/apt/xenial/stable/${libnullpay_pkg_name}
RUN apt-get update -y
RUN dpkg -i /usr/lib/${libnullpay_pkg_name}
RUN rm /usr/lib/${libnullpay_pkg_name}

ADD devops/dev/eas01.txn /var/lib/verity-server/eas01.txn
ADD devops/dev/team1.txn /var/lib/verity-server/team1.txn

RUN /usr/share/libvcx/provision_agent_keys.py https://eas-team1.pdev.evernym.com thiskeyisforthewallet1234 --enterprise-seed 000000000000000000000000Trustee1 > /etc/verity-server/vcxconfig.json
RUN sed -i 's/"genesis_path": "<CHANGE_ME>"/"genesis_path": "\/var\/lib\/verity-server\/team1.txn"/g' /etc/verity-server/vcxconfig.json
RUN sed -i 's/"institution_name": "<CHANGE_ME>"/"institution_name": "test"/g' /etc/verity-server/vcxconfig.json
RUN sed -i 's/"institution_logo_url": "<CHANGE_ME>"/"institution_logo_url": "https:\/\/robohash.org\/verity-server"/g' /etc/verity-server/vcxconfig.json
RUN sed -i '9 a "payment_method": "null",' /etc/verity-server/vcxconfig.json

RUN npm install -g nodemon typescript ts-node

EXPOSE 8080

