'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class Relationship extends Protocol {
  constructor (forRelationship = null, threadId = null, label = null) {
    const msgFamily = 'relationship'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.CONNECTION_INVITATION = 'connection-invitation'

    this.forRelationship = forRelationship
    this.label = label
  }

  async createMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CREATE)
    msg = this._addThread(msg)
    msg.label = this.label
    return msg
  }

  async createMsgPacked (context) {
    return this.getMessageBytes(context, await this.createMsg(context))
  }

  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  async connectionInvitationMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CONNECTION_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  async connectionInvitationMsgPacked (context) {
    return this.getMessageBytes(context, await this.connectionInvitationMsg(context))
  }

  async connectionInvitation (context) {
    await this.sendMessage(context, await this.connectionInvitationMsgPacked(context))
  }
}
