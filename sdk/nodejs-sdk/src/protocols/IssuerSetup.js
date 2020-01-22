'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class IssuerSetup extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'issuer-setup'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames = {
      CREATE: 'create',
      CURRENT_PUBLIC_IDENTIFIER: 'current-public-identifier',
      PUBLIC_IDENTIFIER_CREATED: 'public-identifier-created'
    }
  }

  async createMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CREATE)
    return msg
  }

  async createMsgPacked (context) {
    return this.getMessageBytes(context, await this.createMsg(context))
  }

  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  async currentPublicIdentifierMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CURRENT_PUBLIC_IDENTIFIER)
    return msg
  }

  async currentPublicIdentifierMsgPacked (context) {
    return this.getMessageBytes(context, await this.currentPublicIdentifierMsg(context))
  }

  async currentPublicIdentifier (context) {
    await this.sendMessage(context, await this.currentPublicIdentifierMsgPacked(context))
  }
}
