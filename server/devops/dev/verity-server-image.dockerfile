FROM verity-server
# gitlab.corp.evernym.com:4567/spencer.holman/verity-sdk/verity-server
ADD . /opt/mock-agency/.

WORKDIR /opt/mock-agency
RUN npm config set strict-ssl=false # FIXME: We need to get rid of this!! Pull from real NPM!
RUN npm install
RUN npm install -g ts-node
RUN npm run build
RUN npm config set strict-ssl=true

RUN rm -rf ~/.indy_client
ADD devops/dev/pool_client.txn /var/lib/verity-server/pool.txn

RUN chmod u+x ./devops/dev/verity-entrypoint.sh
ENTRYPOINT ["./devops/dev/verity-entrypoint.sh"]
