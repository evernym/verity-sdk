const Protocol = require('../Protocol')
const utils = require('../../utils')

module.exports = class ConnectingV10 extends Protocol {
  constructor (label, base64InviteURL, threadId = null) {
    const msgFamily = 'connections'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.label = label
    this.base64InviteURL = base64InviteURL

    this.msgNames.SEND_ACCEPT_INVITE = 'accept'
    this.msgNames.GET_STATUS = 'status'
    this.msgNames.REQUEST_RECEIVED = 'request-received'
    this.msgNames.RESPONSE_SENT = 'response-sent'
  }

  async accept (context) {
    await this.sendMessage(context, await this.acceptMsgPacked(context))
  }

  async acceptMsgPascked (context) {
    return this.getMessageBytes(context, this.acceptMsg())
  }

  acceptMsg () {
    var msg = this._getBaseMessage(this.msgNames.SEND_ACCEPT_INVITE)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    msg.label = this.label
    msg.invite_url = this.base64InviteURL
    return msg
  }

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }

  async statusMsgPacked (context) {
    return this.getMessageBytes(context, this.rejectMsg())
  }

  statusMsg () {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }
}
