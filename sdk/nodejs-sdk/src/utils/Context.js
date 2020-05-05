'use strict'
const URL = require('url').URL
const indy = require('./indy')
const utils = require('./index')

const V_0_1 = '0.1'
const V_0_2 = '0.2'

module.exports = class Context {
  constructor () {
    this.version = V_0_2
    if (arguments.length !== 0) {
      throw new Error('Invalid arguments to Context constructor. Context should be created with Context.create or Context.createWithConfig.')
    }
  }

  static async createWithConfig (config = {}) {
    if (typeof config === 'string' || Buffer.isBuffer(config)) {
      config = JSON.parse(config)
    }
    const { version = V_0_1 } = config

    if (V_0_1 === version) {
      const rtn = Context.parseV01(config)
      await rtn.openWallet()
      return rtn
    } else if (V_0_2 === version) {
      const rtn = Context.parseV02(config)
      await rtn.openWallet()
      return rtn
    } else {
      throw new Error(`Invalid context version -- '${version}' is not supported`)
    }
  }

  static parseV01 (config) {
    Context.validateV01Config(config)

    const context = new Context()
    context.verityUrl = config.verityUrl
    context.verityPublicDID = config.verityPublicDID
    context.verityPublicVerKey = config.verityPublicVerkey
    context.domainDID = config.verityPairwiseDID
    context.verityAgentVerKey = config.verityPairwiseVerkey
    context.sdkVerKeyId = config.sdkPairwiseDID
    context.sdkVerKey = config.sdkPairwiseVerkey
    context.endpointUrl = config.endpointUrl
    context.walletName = config.walletName
    context.walletPath = config.walletPath
    context.walletKey = config.walletKey
    context.buildWalletConfig(config.walletName, config.walletPath)
    context.buildWalletCredentails(config.walletKey)
    return context
  }

  static parseV02 (config) {
    Context.validateV02Config(config)

    const context = new Context()
    context.verityUrl = config.verityUrl
    context.verityPublicDID = config.verityPublicDID
    context.verityPublicVerKey = config.verityPublicVerKey
    context.domainDID = config.domainDID
    context.verityAgentVerKey = config.verityAgentVerKey
    context.sdkVerKeyId = config.sdkVerKeyId
    context.sdkVerKey = config.sdkVerKey
    context.endpointUrl = config.endpointUrl
    context.walletName = config.walletName
    context.walletPath = config.walletPath
    context.walletKey = config.walletKey
    context.buildWalletConfig(config.walletName, config.walletPath)
    context.buildWalletCredentails(config.walletKey)
    return context
  }

  static async create (
    walletName,
    walletKey,
    verityUrl,
    domainDID = null,
    verityAgentVerKey = null,
    endpointUrl = null,
    seed = null
  ) {
    const context = new Context()
    context.verityUrl = verityUrl

    if (domainDID) {
      context.domainDID = domainDID
    }

    if (verityAgentVerKey) {
      context.verityAgentVerKey = verityAgentVerKey
    }

    if (endpointUrl) {
      context.endpointUrl = endpointUrl
    }

    context.buildWalletConfig(walletName)
    context.buildWalletCredentails(walletKey)

    await context.updateVerityInfo()
    await context.openWallet();

    [context.sdkVerKeyId, context.sdkVerKey] = await indy.newDid(context, seed)

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
      this.verityPublicVerKey = response.verKey
    } catch (e) {
      console.error(e)
      throw new Error(`Unable to retrieve Verity public DID and Verkey from ${this.verityUrl}`)
    }
  }

  async openWallet () {
    this.walletHandle = await indy.createOrOpenWallet(this.walletConfig, this.walletCredentials)
  }

  static validateV01Config (config) {
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

  static validateV02Config (config) {
    const requiredAttributes = [
      'verityUrl',
      'verityPublicDID',
      'verityPublicVerKey',
      'sdkVerKeyId',
      'sdkVerKey',
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

  async restApiToken () {
    return indy.restApiToken(this)
  }

  async closeWallet () {
    return indy.closeWallet(this.walletHandle)
  }

  async deleteWallet () {
    return indy.deleteWallet(this.walletHandle, this.walletConfig, this.walletCredentials)
  }
}
