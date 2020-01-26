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
    context.walletName = config.walletName
    context.walletPath = config.walletPath
    context.walletKey = config.walletKey
    context.buildWalletConfig(config.walletName, config.walletPath)
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

  buildWalletConfig (walletName, walletPath) {
    this.walletName = walletName
    const walletConfig = { id: walletName }
    if (walletPath) {
      walletConfig.storage_config = { path: walletPath }
    }
    this.walletConfig = JSON.stringify(walletConfig)
  }

  buildWalletCredentails (walletKey) {
    this.walletKey = walletKey
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
      'sdkPairwiseDID',
      'sdkPairwiseVerkey',
      'walletName',
      'walletKey'
    ]
    for (const attr of requiredAttributes) {
      if (config[attr] === undefined || config[attr] === null) {
        throw new Error('Invalid Context Configuration: missing attribute "' + attr + '"')
      }
    }
  }

  getConfig () {
    const config = JSON.parse(JSON.stringify(this))
    delete config.walletConfig
    delete config.walletCredentials
    delete config.walletHandle
    return config
  }

  async closeWallet () {
    return indy.closeWallet(this.walletHandle)
  }

  async deleteWallet () {
    return indy.deleteWallet(this.walletHandle, this.walletConfig, this.walletCredentials)
  }
}
