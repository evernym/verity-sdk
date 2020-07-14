'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class OutOfBand extends Protocol {
  constructor (forRelationship = null, inviteUrl = null) {
    const msgFamily = 'out-of-band'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier)

    this.msgNames.REUSE = 'reuse'

    this.forRelationship = forRelationship
    this.inviteUrl = inviteUrl
  }

  async reuseMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.COMMUNITY_MSG_QUALIFIER)
    msg['~for_relationship'] = this.forRelationship
    msg.inviteUrl = this.inviteUrl
    msg = this._addThread(msg)
    return msg
  }

  async reuseMsgPacked (context) {
    return this.getMessageBytes(context, await this.reuseMsg(context))
  }

  async reuse (context) {
    await this.sendMessage(context, await this.reuseMsgPacked(context))
  }
}
