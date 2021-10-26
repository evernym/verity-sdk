FROM ubuntu:18.04

ENV LIBINDY_VERSION 1.15.0-bionic
ENV VERITY_SERVER https://vas.pps.evernym.com
ENV LANG=C.UTF-8

RUN echo LANG=C.UTF-8 > /etc/default/locale

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

# Adding Evernym ca cert
RUN mkdir -p /usr/local/share/ca-certificates
RUN curl -k https://repo.corp.evernym.com/ca.crt | tee /usr/local/share/ca-certificates/Evernym_Root_CA.crt
RUN update-ca-certificates

# Setup apt for evernym repositories
RUN curl https://repo.corp.evernym.com/repo.corp.evenym.com-sig.key | apt-key add -
RUN add-apt-repository "deb https://repo.corp.evernym.com/deb evernym-agency-dev-ubuntu main"
RUN add-apt-repository "deb https://repo.corp.evernym.com/deb evernym-agency-rc-ubuntu main"

RUN apt-get install -y libvdrtools=0.8.0-bionic

# Install Ngrok
RUN curl -O -s https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && \
    cp ngrok /usr/local/bin/.


# Install NodeJS
RUN curl -sL https://deb.nodesource.com/setup_12.x | bash - && \
    apt-get install -y nodejs


# Install DotNet and Mono
RUN curl -sL https://packages.microsoft.com/config/ubuntu/18.04/packages-microsoft-prod.deb -o /tmp/packages-microsoft-prod.deb && \
    dpkg -i /tmp/packages-microsoft-prod.deb && \
    apt-get update && \
    apt-get install -y dotnet-sdk-3.1
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF && \
    add-apt-repository "deb https://download.mono-project.com/repo/ubuntu stable-bionic main" && \
    apt-get update && \
    apt-get install -y mono-complete


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
WORKDIR /samples/sdk/oob-with-request-attach
RUN npm install 2>/dev/null
RUN npm rebuild 2>/dev/null


# Install .NET packages
WORKDIR /samples/sdk/dotnet-example-app
RUN dotnet build -c Release


# Install static HTTP server to serve generated QR code
RUN npm install -g http-server
WORKDIR /samples/sdk
EXPOSE 4000


# Start Ngrok tunnel for webhook URL in docker entrypoint
ENTRYPOINT ["/samples/sdk/entrypoint.sh"]

