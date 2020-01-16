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

  verityPublicVerkey (verkey) {
    this._verityPublicVerkey = verkey
  }

  verityPairwiseDID (did) {
    this._verityPairwiseDID = did
  }

  verityPairwiseVerkey (verkey) {
    this._verityPairwiseVerkey = verkey
  }

  sdkPairwiseDID (did) {
    this._sdkPairwiseDID = did
  }

  sdkPairwiseVerkey (verkey) {
    this._sdkPairwiseVerkey = verkey
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
      context = await Context.create(this._walletName, this._walletKey, this._verityUrl)
    }
    context.verityPublicDID = this._verityPublicDID
    context.verityPublicVerkey = this._verityPublicVerkey
    context.verityPairwiseDID = this._verityPairwiseDID
    context.verityPairwiseVerkey = this._verityPairwiseVerkey
    context.sdkPairwiseDID = this._sdkPairwiseDID
    context.sdkPairwiseVerkey = this.sdkPairwiseVerkey
    context.endpointUrl = this.endpointUrl
    return context
  }
}
