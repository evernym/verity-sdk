"use strict";
const sdk = require('indy-sdk')

exports.init = async function() {
    return sdk.setDefaultLogger('debug')
}

exports.createOrOpenWallet = async function(config, credentials) {
    try {
        await sdk.createWallet(config, credentials)
    } catch(e) {
        if(e.message !== "WalletAlreadyExistsError") {
            throw e;
        }
    }
    return sdk.openWallet(config, credentials)
}

exports.deleteWallet = async function(walletHandle, walletConfig, walletCredentials) {
    await sdk.closeWallet(walletHandle)
    await sdk.deleteWallet(walletConfig, walletCredentials)
}