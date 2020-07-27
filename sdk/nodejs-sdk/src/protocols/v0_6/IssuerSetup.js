'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/**
 * An interface for controlling a 0.6 IssuerSetup protocol.
 * @extends Protocol
 */
class IssuerSetup extends Protocol {
  /**
   * Constructor for the 0.6 IssuerSetup object. This constructor creates an object that is ready to start the setup
   * process of an issuer.
   * @param threadId the thread id of the already started protocol
   * @return 0.6 IssuerSetup object
   *
   * @property {String} msgFamily - 'issuer-setup'
   * @property {String} msgFamilyVersion - '0.6'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.CREATE - 'create'
   * @property {String} this.msgNames.PUBLIC_IDENTIFIER - 'public-identifier'
   * @property {String} this.msgNames.CURRENT_PUBLIC_IDENTIFIER - 'current-public-identifier'
   * @property {String} this.msgNames.PUBLIC_IDENTIFIER_CREATED - 'public-identifier-created'
   */
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
module.exports = IssuerSetup
