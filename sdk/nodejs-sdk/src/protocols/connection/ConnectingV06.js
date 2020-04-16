'use strict'
const uuid = require('uuid')
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class ConnectingV06 extends Protocol {
  constructor (threadId = null, sourceId = uuid(), phoneNumber = null, includePublicDID = false) {
    const msgFamily = 'connecting'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.sourceId = sourceId
    this.phoneNumber = phoneNumber
    this.includePublicDID = includePublicDID

    this.msgNames.CREATE_CONNECTION = 'CREATE_CONNECTION'
    this.msgNames.INVITE_DETAIL = 'CONN_REQUEST_RESP'
    this.msgNames.CONN_REQ_ACCEPTED = 'CONN_REQ_ACCEPTED'
    this.msgNames.GET_STATUS = 'get-status'
  }

  async connectMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CREATE_CONNECTION)
    msg.sourceId = this.sourceId
    msg.phoneNo = this.phoneNo
    msg.includePublicDID = this.includePublicDID
    return msg
  }

  async connectMsgPacked (context) {
    return this.getMessageBytes(context, await this.connectMsg(context))
  }

  async connect (context) {
    await this.sendMessage(context, await this.connectMsgPacked(context))
  }

  async statusMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg.sourceId = this.sourceId
    return msg
  }

  async statusMsgPacked (context) {
    return this.getMessageBytes(context, await this.statusMsg(context))
  }

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }
}
