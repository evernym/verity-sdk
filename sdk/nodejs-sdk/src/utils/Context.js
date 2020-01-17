'use strict'
const URL = require('url').URL
const indy = require('./indy')
const utils = require('./index')

module.exports = class Context {
  constructor () {
    if (arguments.length !== 0) {
      throw new Error('Invalid arguments to Context constructor. Context should be created with Context.create or Context.createWithConfig.')
    }
  }

  static async createWithConfig (config = {}) {
    if (typeof config === 'string') {
      config = JSON.parse(config)
    }
    Context.validateConfig(config)

    const context = new Context()
    context.verityUrl = config.verityUrl
    context.verityPublicDID = config.verityPublicDID
    context.verityPublicVerkey = config.verityPublicVerkey
    context.verityPairwiseDID = config.verityPairwiseDID
    context.verityPairwiseVerkey = config.verityPairwiseVerkey
    context.sdkPairwiseDID = config.sdkPairwiseDID
    context.sdkPairwiseVerkey = config.sdkPairwiseVerkey
    context.endpointUrl = config.endpointUrl
    context.buildWalletConfig(config.walletName)
    context.buildWalletCredentails(config.walletKey)
    await context.openWallet()
    return context
  }

  static async create (walletName, walletKey, verityUrl, endpointUrl) {
    const context = new Context()
    context.buildWalletConfig(walletName)
    context.buildWalletCredentails(walletKey)
    context.verityUrl = verityUrl
    context.endpointUrl = endpointUrl
    await context.updateVerityInfo()
    await context.openWallet()
    return context
  }

  buildWalletConfig (walletName) {
    this.walletConfig = JSON.stringify({ id: walletName })
  }

  buildWalletCredentails (walletKey) {
    this.walletCredentials = JSON.stringify({ key: walletKey })
  }

  async updateVerityInfo () {
    try {
      const url = new URL(this.verityUrl)
      url.pathname = '/agency'
      const response = await utils.httpGet(url.href)
      this.verityPublicDID = response.DID
      this.verityPublicVerkey = response.verKey
    } catch (e) {
      console.error(e)
      throw new Error(`Unable to retrieve Verity public DID and Verkey from ${this.verityUrl}`)
    }
  }

  async openWallet () {
    this.walletHandle = await indy.createOrOpenWallet(this.walletConfig, this.walletCredentials)
  }

  static validateConfig (config) {
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
    for (const attr of requiredAttributes) {
      if (config[attr] === undefined || config[attr] === null) {
        throw new Error('Invalid Context Configuration: missing attribute "' + attr + '"')
      }
    }
  }

  async closeWallet () {
    return indy.closeWallet(this.walletHandle)
  }

  async deleteWallet () {
    return indy.deleteWallet(this.walletHandle, this.walletConfig, this.walletCredentials)
  }
}
