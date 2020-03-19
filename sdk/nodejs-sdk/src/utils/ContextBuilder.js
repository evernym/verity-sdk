'use strict'
const Context = require('./Context')

module.exports = class ContextBuilder {
  constructor (context = null) {
    if (context != null) {
      this.context = context
    }
  }

  verityUrl (verityUrl) {
    this._verityUrl = verityUrl
  }

  verityPublicDID (did) {
    this._verityPublicDID = did
  }

  verityPublicVerKey (verkey) {
    this._verityPublicVerKey = verkey
  }

  domainDID (did) {
    this._domainDID = did
  }

  verityAgentVerKey (verkey) {
    this._verityAgentVerKey = verkey
  }

  sdkVerKeyId (did) {
    this._sdkVerKeyId = did
  }

  sdkVerKey (verkey) {
    this._sdkVerKey = verkey
  }

  endpointUrl (url) {
    this._endpointUrl = url
  }

  walletName (name) {
    this._walletName = name
  }

  walletKey (key) {
    this._walletKey = key
  }

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
