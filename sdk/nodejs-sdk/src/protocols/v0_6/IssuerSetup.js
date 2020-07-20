'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/**
 * An interface for controlling a 0.6 IssuerSetup protocol.
 */
module.exports = class IssuerSetup extends Protocol {
  constructor (threadId = null) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'issuer-setup'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '0.6'
     /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.PUBLIC_IDENTIFIER = 'public-identifier'
    this.msgNames.CURRENT_PUBLIC_IDENTIFIER = 'current-public-identifier'
    this.msgNames.PUBLIC_IDENTIFIER_CREATED = 'public-identifier-created'
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #create
     */
  async createMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CREATE)
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #create
     */
  async createMsgPacked (context) {
    return this.getMessageBytes(context, await this.createMsg(context))
  }

  /**
     * Directs verity-application to start and create an issuer identity and set it up
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #currentPublicIdentifier
     */
  async currentPublicIdentifierMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CURRENT_PUBLIC_IDENTIFIER)
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #currentPublicIdentifier
     */
  async currentPublicIdentifierMsgPacked (context) {
    return this.getMessageBytes(context, await this.currentPublicIdentifierMsg(context))
  }

  /**
     * Asks the verity-application for the current issuer identity that is setup.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async currentPublicIdentifier (context) {
    await this.sendMessage(context, await this.currentPublicIdentifierMsgPacked(context))
  }
}
