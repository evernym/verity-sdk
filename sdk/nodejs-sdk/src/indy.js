'use strict'
const sdk = require('indy-sdk')
let initialized = false

function init() {
  if(!initialized) {
    sdk.setDefaultLogger('warn')
    initialized = true;
  }
}

exports.createOrOpenWallet = async function (config, credentials) {
  init()
  try {
    await sdk.createWallet(config, credentials)
  } catch (e) {
    if (e.message !== 'WalletAlreadyExistsError') {
      throw e
    }
  }
  return sdk.openWallet(config, credentials)
}

exports.deleteWallet = async function (walletHandle, walletConfig, walletCredentials) {
  init()
  await sdk.closeWallet(walletHandle)
  await sdk.deleteWallet(walletConfig, walletCredentials)
}
