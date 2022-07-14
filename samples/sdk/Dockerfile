FROM ubuntu:18.04

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
        vim && \
        # Install libvdrtools
        curl https://gitlab.com/evernym/verity/vdr-tools/-/package_files/27311920/download --output libvdrtools_0.8.4-bionic_amd64.deb && \
        apt-get install -y ./libvdrtools_0.8.4-bionic_amd64.deb && \
        # Install NodeJS
        curl -s https://deb.nodesource.com/gpgkey/nodesource.gpg.key | apt-key add - && \
        echo "deb https://deb.nodesource.com/node_12.x bionic main\ndeb-src https://deb.nodesource.com/node_12.x bionic main" > /etc/apt/sources.list.d/nodesource.list && \
        apt-get update && \
        apt-get install -y nodejs && \
        # Install DotNet and Mono
        curl -sL https://packages.microsoft.com/config/ubuntu/18.04/packages-microsoft-prod.deb -o /tmp/packages-microsoft-prod.deb && \
        dpkg -i /tmp/packages-microsoft-prod.deb && \
        apt-get update && \
        apt-get install -y dotnet-sdk-3.1 && \
        apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 3FA7E0328081BFF6A14DA29AA6A19B38D3D831EF && \
        add-apt-repository "deb https://download.mono-project.com/repo/ubuntu stable-bionic main" && \
        apt-get update && \
        apt-get install -y mono-complete && \
        rm -rf /var/lib/apt/lists/*


# Install Ngrok
RUN curl -O -s https://bin.equinox.io/c/4VmDzA7iaHb/ngrok-stable-linux-amd64.zip && \
    unzip ngrok-stable-linux-amd64.zip && \
    cp ngrok /usr/local/bin/.


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

