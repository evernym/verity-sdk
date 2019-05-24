FROM verity-server
# gitlab.corp.evernym.com:4567/spencer.holman/verity-sdk/verity-server
ADD . /opt/mock-agency/.

WORKDIR /opt/mock-agency
RUN npm config set strict-ssl=false # FIXME: We need to get rid of this!! Pull from real NPM!
RUN npm install
RUN npm install -g ts-node
RUN npm run build

CMD ["sh", "-c", "nodemon ./build/src/app.js"]