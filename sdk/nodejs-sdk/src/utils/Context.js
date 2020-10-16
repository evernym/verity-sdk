'use strict'
const URL = require('url').URL
const indy = require('./indy')
const utils = require('./index')

const V_0_1 = '0.1'
const V_0_2 = '0.2'

/**
 * This Context object holds the data for accessing an agent on a verity-application. A complete and correct data in
 * the context allows for access and authentication to that agent.
 */
class Context {
  /*
   * Context Members
   * @property verityPublicDID verity's public did
   * @property verityPublicVerKey verity's public verkey
   * @property verityAgentVerKey
   * @property sdkPairwiseDID - sdk's did in a connection
   * @property sdkPairwiseVerkey - sdk's verkey in a connection
   * @property walletName wallet name
   * @property walletPath path for the wallet
   * @property walletKey wallet key
   * @property verityUrl The url for the verity-application that this object is connected to
   * @property endpointUrl the endpoint for receiving messages from the agent on the verity-application
   * @property domainDid The identifier for a identity domain. This identifier identifies a self-sovereign Identity. It will be common
   *    across all agents and controllers of that Identity.
   * @property verityAgentVerKey the verkey for the agent on the verity-application
   * @property endpointUrl the endpoint for receiving messages from the agent on the verity-application
   * @property seed seed used to create did
   */
  constructor () {
    this.version = V_0_2
    if (arguments.length !== 0) {
      throw new Error('Invalid arguments to Context constructor. Context should be created with Context.create or Context.createWithConfig.')
    }
  }

  /**
   * @static creates a Context from a config
   * @param config
   */
  static async createWithConfig (config = {}) {
    if (typeof config === 'string' || Buffer.isBuffer(config)) {
      config = JSON.parse(config)
    }
    const { version = V_0_1 } = config

    if (V_0_1 === version) {
      const rtn = Context.parseV01(config)
      rtn.walletHandle = await indy.openWallet(rtn.walletConfig, rtn.walletCredentials)
      return rtn
    } else if (V_0_2 === version) {
      const rtn = Context.parseV02(config)
      rtn.walletHandle = await indy.openWallet(rtn.walletConfig, rtn.walletCredentials)
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

  /**
   * @static creates a Context object
   * @param walletName wallet name
   * @param walletKey wallet key
   * @param verityUrl The url for the verity-application that this object is connected to
   * @param domainDid The identifier for a identity domain. This identifier identifies a self-sovereign Identity. It will be common
   * across all agents and controllers of that Identity.
   * @param verityAgentVerKey the verkey for the agent on the verity-application
   * @param endpointUrl the endpoint for receiving messages from the agent on the verity-application
   * @param seed seed used to create did
   * @param walletPath custom path where libindy wallet should be stored (default is ~/.indy_client/wallet)
   */
  static async create (
    walletName,
    walletKey,
    verityUrl,
    domainDID = null,
    verityAgentVerKey = null,
    endpointUrl = null,
    seed = null,
    walletPath = null
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

    if (walletPath) {
      context.walletPath = walletPath
    }

    context.buildWalletConfig(walletName, walletPath)
    context.buildWalletCredentails(walletKey)

    await context.updateVerityInfo()
    context.walletHandle = await indy.createAndOpenWallet(context.walletConfig, context.walletCredentials);

    [context.sdkVerKeyId, context.sdkVerKey] = await indy.newDid(context, seed)

    return context
  }

  /**
   * Sets the wallet config
   * @param {String} walletName
   * @param {String} walletPath
   */
  buildWalletConfig (walletName, walletPath) {
    this.walletName = walletName
    const walletConfig = { id: walletName }
    if (walletPath) {
      walletConfig.storage_config = { path: walletPath }
    }
    this.walletConfig = JSON.stringify(walletConfig)
  }

  /**
   * Sets the wallet credentials
   * @param {String} walletKey
   */
  buildWalletCredentails (walletKey) {
    this.walletKey = walletKey
    this.walletCredentials = JSON.stringify({ key: walletKey })
  }

  /**
   * updates verity DID and VerKey by getting it from the verityUrl previously set
   */
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

  /**
   * @static validates version 1 of the configuration
   */
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

  /**
   * @static validates version 2 of the configuration
   */
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

  /**
   * @return config
   */
  getConfig () {
    const config = JSON.parse(JSON.stringify(this))
    delete config.walletConfig
    delete config.walletCredentials
    delete config.walletHandle
    return config
  }

  /**
   * Converts the local keys held in the context to REST api token. This token can be used with the REST API for the
   * verity-application
   * @return a REST API token
   */
  async restApiToken () {
    return indy.restApiToken(this)
  }

  /**
   * Closes the the open wallet handle stored inside the Context object.
   */
  async closeWallet () {
    return indy.closeWallet(this.walletHandle)
  }

  /**
   * Deletes the open wallet handle stored inside the Context object.
   */
  async deleteWallet () {
    return indy.deleteWallet(this.walletHandle, this.walletConfig, this.walletCredentials)
  }
}
module.exports = Context
