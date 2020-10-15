'use strict'
const sdk = require('indy-sdk')
const bs58 = require('bs58')
let initialized = false

/** Sets default logger */
function init () {
  if (!initialized) {
    sdk.setDefaultLogger('warn')
    initialized = true
  }
}

exports.init = init
exports.sdk = sdk

/**
 * Creates wallet if not already and opens it
 * @param config wallet configuration
 * @param credentials wallet credentials
 */
exports.createAndOpenWallet = async function (config, credentials) {
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

/**
 * Creates wallet if not already created
 * @param config wallet configuration
 * @param credentials wallet credentials
 */
exports.createWallet = async function (config, credentials) {
  init()
  try {
    await sdk.createWallet(config, credentials)
  } catch (e) {
    if (e.message !== 'WalletAlreadyExistsError') {
      throw e
    }
  }
}

/**
 * Opens already existing wallet
 * @param config wallet configuration
 * @param credentials wallet credentials
 */
exports.openWallet = async function (config, credentials) {
  init()
  return sdk.openWallet(config, credentials)
}

/**
 * Closes a wallet
 * @param walletHandle handle to access the wallet
 */
exports.closeWallet = async function (walletHandle) {
  init()
  await sdk.closeWallet(walletHandle)
}

/**
 * Deletes a wallet
 * @param walletHandle handle to access the wallet
 * @param walletConfig wallet configuration
 * @param walletCredentials wallet credentials
 */
exports.deleteWallet = async function (walletHandle, walletConfig, walletCredentials) {
  init()
  await sdk.closeWallet(walletHandle)
  await sdk.deleteWallet(walletConfig, walletCredentials)
}

/**
 * creates a new DID
 * @param context an instance of the Context object initialized to a verity-application agent
 * @param seed
 * @return did, verkey pair
 */
exports.newDid = async function (context, seed = null) {
  init()
  var param = {}
  if (seed) {
    param.seed = seed
  }
  return sdk.createAndStoreMyDid(context.walletHandle, param)
}

/**
 * @param context an instance of the Context object initialized to a verity-application agent
 * @return restApiToken
 */
exports.restApiToken = async function (context) {
  init()
  const t = bs58.encode(
    await sdk.cryptoSign(context.walletHandle, context.sdkVerKey, Buffer.from(context.sdkVerKey, 'utf-8'))
  )
  return context.sdkVerKey + ':' + t
}
