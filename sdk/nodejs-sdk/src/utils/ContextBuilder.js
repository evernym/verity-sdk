'use strict'
const Context = require('./Context')

/** A ContextBuilder */
class ContextBuilder {
  /** @param {ContextBuilder} */
  constructor (context = null) {
    if (context != null) {
      this.context = context
    }
  }

  /**
   * set verityUrl
   * @param verityUrl The url for the verity-application that this object is connected to
   */
  verityUrl (verityUrl) {
    this._verityUrl = verityUrl
  }

  /**
   * Set the public identifier for the verity-application. This identifier is unique for each verity-application instance
   * but is common for all agents on that instance.
   * @param did the public identifier (DID) for a verity-application instance
   */
  verityPublicDID (did) {
    this._verityPublicDID = did
  }

  /**
   * Set the public verkey for the verity-application. This verkey is unique for each verity-application instance
   * but is common for all agents on that instance.
   * @param verkey the public verkey for a verity-application instance
   */
  verityPublicVerKey (verkey) {
    this._verityPublicVerKey = verkey
  }

  /**
   * Set the identifier for a identity domain. This identifier identifies a self-sovereign Identity. It will be common
   * across all agents and controllers of that Identity.
   * @param did the Domain identifier (DID)
   */
  domainDID (did) {
    this._domainDID = did
  }

  /**
   * Set the verkey for the agent on the verity-application
   * @param verkey the verkey for the verity-application agent
   */
  verityAgentVerKey (verkey) {
    this._verityAgentVerKey = verkey
  }

  /**
   * @param did the id for the sdk verkey
   */
  sdkVerKeyId (did) {
    this._sdkVerKeyId = did
  }

  /**
   * Set the verkey for the locally held key-pair. A corresponding private key is held in the wallet for this verkey
   * @param verkey the verkey for the sdk
   */
  sdkVerKey (verkey) {
    this._sdkVerKey = verkey
  }

  /**
   * Set the endpoint for receiving messages from the agent on the verity-application. This endpoint must be registered
   * using the UpdateEndpoint protocol to effectively change it.
   * @param url the endpoint contained in this context
   */
  endpointUrl (url) {
    this._endpointUrl = url
  }

  /**
   * Set the wallet name
   * @param name walletName
   */
  walletName (name) {
    this._walletName = name
  }

  /**
   * Set the wallet key
   * @param key
   */
  walletKey (key) {
    this._walletKey = key
  }

  /**
   * Builds a ContextBuilder based on this context. Since Context is immutable, this allows for changes by
   * building a new Context using a ContextBuilder
   * @return a ContextBuilder based on this context
   */
  async build () {
    let context
    if (this.context != null) {
      context = this.context
    } else {
      context = await Context.create(this._walletName, this._walletKey, this._verityUrl, this._endpointUrl)
    }
    context.verityPublicDID = this._verityPublicDID
    context.verityPublicVerKey = this._verityPublicVerKey
    context.domainDID = this._domainDID
    context.verityAgentVerKey = this._verityAgentVerKey
    context.sdkVerKeyId = this._sdkVerKeyId
    context.sdkVerKey = this.sdkVerKey
    return context
  }
}
module.exports = ContextBuilder
