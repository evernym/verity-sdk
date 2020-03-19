'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class Provision extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'agent-provisioning'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE_AGENT = 'CREATE_AGENT'
  }

  async provisionSdkMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CREATE_AGENT)
    msg.fromDID = context.sdkVerKeyId
    msg.fromDIDVerKey = context.sdkVerKey
    return msg
  }

  async provisionSdkMsgPacked (context) {
    const msg = await this.provisionSdkMsg(context)
    return utils.packMessage(
      context.walletHandle,
      msg,
      context.verityPublicDID,
      context.verityPublicVerKey,
      context.sdkVerKey,
      context.verityPublicVerKey
    )
  }

  async provisionSdk (context) {
    const packedMessage = await this.provisionSdkMsgPacked(context)
    const rawResponse = await utils.sendPackedMessage(context, packedMessage)
    const jweBytes = Buffer.from(rawResponse, 'utf8')
    const response = await utils.unpackMessage(context, jweBytes)
    context.domainDID = response.withPairwiseDID
    context.verityAgentVerKey = response.withPairwiseDIDVerKey
    return context
  }
}
