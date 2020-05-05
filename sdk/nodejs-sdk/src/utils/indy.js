'use strict'
const sdk = require('indy-sdk')
const bs58 = require('bs58')
let initialized = false

function init () {
  if (!initialized) {
    sdk.setDefaultLogger('warn')
    initialized = true
  }
}

exports.init = init
exports.sdk = sdk

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

exports.closeWallet = async function (walletHandle) {
  init()
  await sdk.closeWallet(walletHandle)
}

exports.deleteWallet = async function (walletHandle, walletConfig, walletCredentials) {
  init()
  await sdk.closeWallet(walletHandle)
  await sdk.deleteWallet(walletConfig, walletCredentials)
}

exports.newDid = async function (context, seed = null) {
  init()
  var param = {}
  if (seed) {
    param.seed = seed
  }
  return sdk.createAndStoreMyDid(context.walletHandle, param)
}

exports.restApiToken = async function (context) {
  init()
  const t = bs58.encode(
    await sdk.cryptoSign(context.walletHandle, context.sdkVerKey, Buffer.from(context.sdkVerKey, 'utf-8'))
  )
  return context.sdkVerKey + ':' + t
}
