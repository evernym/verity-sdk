FROM verity-server
ADD . /opt/mock-agency/.

WORKDIR /opt/mock-agency
RUN npm config set strict-ssl=false # FIXME: We need to get rid of this!! Pull from real NPM!
RUN npm install
RUN npm install -g ts-node

CMD ["sh", "-c", "nodemon ./build/src/app.js"] 