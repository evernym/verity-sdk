'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
const indy = require('indy-sdk')

/**
 * An interface for controlling a 0.7 Provision protocol.
 * @extends Protocol
 */
class Provision extends Protocol {
  /**
   * Constructor for the 1.0 Provision object. This constructor creates an object that is ready to create a new
   * agent.
   *
   * @param token an agent creation token that is provided by Evernym that authorize the creation of an agent
   * @param threadId the thread id of the already started protocol
   * @return 0.7 Provision object
   *
   * @property {String} msgFamily - 'agent-provisioning'
   * @property {String} msgFamilyVersion - '0.7'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.CREATE_EDGE_AGENT - 'create-edge-agent'
   */
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

  /** Provision 0.7 protocol interface class */
  async sendToVerity (context, packedMsg) {
    const rawResponse = await utils.sendPackedMessage(context, packedMsg)
    const jweBytes = Buffer.from(rawResponse, 'utf8')
    return utils.unpackMessage(context, jweBytes)
  }

  /**
   * Creates the control message without packaging and sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @return the constructed message (JSON object)
   *
   * @see #provision
   */
  provisionMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.CREATE_EDGE_AGENT)
    msg.requesterVk = context.sdkVerKey
    if (this.token != null) {
      msg.provisionToken = this.token
    }
    return msg
  }

  /**
   * Creates and packages message without sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @return the byte array ready for transport
   *
   * @see #provision
   */
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

  /**
   * Sends provisioning message that directs the creation of an agent to the to verity-application
   * @param context an instance of the Context object initialized to a verity-application agent
   * @return new Context with provisioned details
   */
  async provision (context) {
    if (this.token != null) {
      await this.validateToken(this.token)
    }

    const packedMsg = await this.provisionMsgPacked(context)
    const response = await this.sendToVerity(context, packedMsg)
    if (response['@type'] === this._getMessageType(this.msgNames.PROBLEM_REPORT)) {
      throw new Error(response.msg)
    }
    context.domainDID = response.selfDID
    context.verityAgentVerKey = response.agentVerKey
    return context
  }
}
module.exports = Provision
