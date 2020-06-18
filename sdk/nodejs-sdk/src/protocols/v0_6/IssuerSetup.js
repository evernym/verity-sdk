'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class IssuerSetup extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'issuer-setup'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.PUBLIC_IDENTIFIER = 'public-identifier'
    this.msgNames.CURRENT_PUBLIC_IDENTIFIER = 'current-public-identifier'
    this.msgNames.PUBLIC_IDENTIFIER_CREATED = 'public-identifier-created'
  }

  async createMsg (context) {
    msg = this._getBaseMessage(this.msgNames.CREATE)
    msg = this._addThread(msg)
    return msg
  }

  async createMsgPacked (context) {
    return this.getMessageBytes(context, await this.createMsg(context))
  }

  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  async currentPublicIdentifierMsg (context) {
    msg = this._getBaseMessage(this.msgNames.CURRENT_PUBLIC_IDENTIFIER)
    msg = this._addThread(msg)
    return msg
  }

  async currentPublicIdentifierMsgPacked (context) {
    return this.getMessageBytes(context, await this.currentPublicIdentifierMsg(context))
  }

  async currentPublicIdentifier (context) {
    await this.sendMessage(context, await this.currentPublicIdentifierMsgPacked(context))
  }
}
