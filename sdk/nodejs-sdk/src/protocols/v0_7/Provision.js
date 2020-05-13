'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class Provision extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'agent-provisioning'
    const msgFamilyVersion = '0.7'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE_AGENT = 'CREATE_AGENT'
  }

  async sendToVerity (context, packedMsg) {
    const rawResponse = await utils.sendPackedMessage(context, packedMsg)
    const jweBytes = Buffer.from(rawResponse, 'utf8')
    return utils.unpackMessage(context, jweBytes)
  }

  provisionMsg (context) {
    const keys = {
      fromVerKey: context.sdkVerKey
    }
    const msg = this._getBaseMessage(this.msgNames.CREATE_AGENT)
    msg.requesterKeys = keys
    return msg
  }

  async provisionMsgPacked (context) {
    const msg = this.provisionMsg(context)
    return utils.packMessage(
      context.walletHandle,
      msg,
      context.verityPublicDID,
      context.verityPublicVerKey,
      context.sdkVerKey,
      context.verityPublicVerKey
    )
  }

  async provision (context) {
    const packedMsg = await this.provisionMsgPacked(context)
    const response = await this.sendToVerity(context, packedMsg)
    context.domainDID = response.selfDID
    context.verityAgentVerKey = response.agentVerKey
    return context
  }
}
