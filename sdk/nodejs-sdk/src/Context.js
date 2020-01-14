"use strict";
const indy = require('./indy')

module.exports = class Context {
    verityUrl;
    verityPublicDID;
    verityPublicVerkey;
    verityPairwiseDID;
    verityPairwiseVerkey;
    sdkPairwiseDID;
    sdkPairwiseVerkey;
    endpointUrl;
    walletConfig;
    walletCredentials;
    walletHandle;

    static async create(configString) {
        const config = JSON.parse(configString)
        Context.validateConfig(config)

        const context = new Context()
        context.verityUrl = config.verityUrl;
        context.verityPublicDID = config.verityPublicDID;
        context.verityPublicVerkey = config.verityPublicVerkey;
        context.verityPairwiseDID = config.verityPairwiseDID;
        context.verityPairwiseVerkey = config.verityPairwiseVerkey;
        context.sdkPairwiseDID = config.sdkPairwiseDID;
        context.sdkPairwiseVerkey = config.sdkPairwiseVerkey;
        context.endpointUrl = config.endpointUrl;
        context.walletConfig = JSON.stringify({id: config.walletName})
        context.walletCredentials = JSON.stringify({key: config.walletKey})
        context.walletHandle = await indy.createOrOpenWallet(context.walletConfig, context.walletCredentials)

        return context
    }

    static validateConfig(config) {
        const requiredAttributes = [
            'verityUrl', 
            'verityPublicDID',
            'verityPublicVerkey',
            'verityPairwiseDID',
            'verityPairwiseVerkey',
            'sdkPairwiseDID',
            'sdkPairwiseVerkey',
            'endpointUrl',
            'walletName',
            'walletKey'
        ]
        for(let attr of requiredAttributes) {
            if(config[attr] == undefined || config[attr] == null) {
                throw new Error('Invalid Context Configuration: missing attribute "' + attr + '"')
            }
        }
    }

    async deleteWallet() {
        return indy.deleteWallet(this.walletHandle, this.walletConfig, this.walletCredentials)
    }
}


