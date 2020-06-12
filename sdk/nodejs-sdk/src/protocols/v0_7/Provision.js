'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
const indy = require('indy-sdk')

module.exports = class Provision extends Protocol {
  constructor (threadId = null, token = null) {
    const msgFamily = 'agent-provisioning'
    const msgFamilyVersion = '0.7'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE_EDGE_AGENT = 'create-edge-agent'

    this.token = JSON.parse(token)
  }

  async validateToken (token) {
    const concatStr = (
      token.nonce +
      token.timestamp +
      token.sponseeId +
      token.sponsorId
    )
    const data = Buffer.from(concatStr, 'utf-8')
    const sig = Buffer.from(token.sig, 'base64')

    const valid = await indy.cryptoVerify(token.sponsorVerKey, data, sig)

    if (valid === false) {
      throw new Error('Invalid provision token -- signature does not validate')
    }
  }

  async sendToVerity (context, packedMsg) {
    const rawResponse = await utils.sendPackedMessage(context, packedMsg)
    const jweBytes = Buffer.from(rawResponse, 'utf8')
    return utils.unpackMessage(context, jweBytes)
  }

  provisionMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CREATE_EDGE_AGENT)
    msg.requesterVk = context.sdkVerKey
    if (this.token != null) {
      msg.provisionToken = this.token
    }
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
    if (this.token != null) {
      await this.validateToken(this.token)
    }

    const packedMsg = await this.provisionMsgPacked(context)
    const response = await this.sendToVerity(context, packedMsg)
    context.domainDID = response.selfDID
    context.verityAgentVerKey = response.agentVerKey
    return context
  }
}
