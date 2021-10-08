Verity is running in a three node Akka cluster split across two independent AWS availability zones based in Frankfurt. 

There are multiple Verity environments depending on your goals. Each environment hosts a set of Verity services that you can use depending on the Evernym products you are leveraging.

You should protect your webhooks from accepting connections to unknown parties. You can do that by whitelisting the IP addresses indicated here. Alternatively, you can protect your webhook with OAuth and configure Verity to provide the OAuth credentials with each response. This is documented in the [UpdateEndpointAuth](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#/UpdateEndpointAuth) schema in the [updateEndpoint REST API](https://app.swaggerhub.com/apis/evernym/verity-rest-api/1.0#/UpdateEndpoint/updateEndpoint) of the Swagger API documentation.


# DEMO

This environment is for building and testing your application. It is attached to Sovrin StagingNet. It is the default environment.

## Verity Application Service (VAS)
The service that drives Verity SaaS. This is most likely the service you need.

### API Endpoint
https://vas.pps.evernym.com

### IP Addresses for Webhook Whitelists
pvas-ore-ve001 44.230.110.185 \
pvas-ore-ve002 35.166.63.87 \
pvas-ore-ve003 35.80.156.43 \


## Consumer Agency Service (CAS)
Used when integrating with the Evernym Mobile SDK

### IP Addresses for Webhook Whitelists
pcas-ore-ve001 34.210.191.37 \
pcas-ore-ve002 44.231.240.123 \
pcas-ore-ve003 35.81.146.64 \


## Enterprise Agency Service (EAS)
Legacy environment used by self-hosted Verity 1 / LibVCX

### IP Addresses for Webhook Whitelists
peas-ore-ve001 52.26.236.159 \
peas-ore-ve002 35.81.215.155 \
peas-ore-ve003 34.214.220.195 \


# PROD

This environment is for production use. It is attached to Sovrin MainNet. You need to specifically request a tenant in the production environment if you want to use it.


## Verity Application Service (VAS)
The service that drives Verity SaaS. This is most likely the service you need.

### API Endpoint
https://vas.evernym.com

### IP Addresses for Webhook Whitelists
avas-frk-pe001 35.156.27.211
avas-frk-pe002 18.196.203.94 <--NEW
avas-frk-pe003 3.65.216.242 <--NEW


## Consumer Agency Service (CAS)
Used when integrating with the Evernym Mobile SDK

### IP Addresses for Webhook Whitelists
acas-frk-pe001 35.157.43.126
acas-frk-pe002 18.195.149.109
acas-frk-pe003 18.198.174.167


## Enterprise Agency Service (EAS)
Legacy environment used by self-hosted Verity 1 / LibVCX

### IP Addresses for Webhook Whitelists
aeas-frk-pe001 52.59.149.255
aeas-frk-pe002 3.66.12.188 <--NEW (only for customers using Verity1/libcx)
aeas-frk-pe003 3.64.236.130 <--NEW (only for customers using Verity1/libcx)
