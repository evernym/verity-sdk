'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/**
 * An interface for controlling a 0.7 IssuerSetup protocol.
 * @extends Protocol
 */
class IssuerSetup extends Protocol {
  /**
   * Constructor for the 0.7 IssuerSetup object. This constructor creates an object that is ready to start the setup
   * process of an issuer.
   * @param threadId the thread id of the already started protocol
   * @return 0.7 IssuerSetup object
   *
   * @property {String} msgFamily - 'issuer-setup'
   * @property {String} msgFamilyVersion - '0.7'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.CREATE - 'create'
   * @property {String} this.msgNames.PUBLIC_IDENTIFIER - 'public-identifier'
   * @property {String} this.msgNames.CURRENT_PUBLIC_IDENTIFIER - 'current-public-identifier'
   * @property {String} this.msgNames.PUBLIC_IDENTIFIER_CREATED - 'public-identifier-created'
   */
  constructor (threadId = null) {
    const msgFamily = 'issuer-setup'
    const msgFamilyVersion = '0.7'
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
     * @param ledgerPrefix a string indicating the location that the issuer identifier should be published to. Verity can publish to the following locations, indicated by the values in quotes:
     *                     [Sovrin Builder Net: "did:indy:sovrin:builder",
     *                     Sovrin Staging Net: "did:indy:sovrin:staging",
     *                     Sovrin Main Net: "did:indy:sovrin"]
     *                     The locations which are available to your Verity tenant will be configured based on your customer agreement.
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     * @return the constructed message (JSON object)
     *
     * @see #create
     */
  async createMsg (context, ledgerPrefix, endorser) {
    let msg = this._getBaseMessage(this.msgNames.CREATE)
    msg = this._addThread(msg)
    msg.ledgerPrefix = ledgerPrefix
    if (endorser) {
      msg.endorser = endorser
    }
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the location that the issuer identifier should be published to. Verity can publish to the following locations, indicated by the values in quotes:
     *                     [Sovrin Builder Net: "did:indy:sovrin:builder",
     *                     Sovrin Staging Net: "did:indy:sovrin:staging",
     *                     Sovrin Main Net: "did:indy:sovrin"]
     *                     The locations which are available to your Verity tenant will be configured based on your customer agreement.
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     * @return the byte array ready for transport
     *
     * @see #create
     */
  async createMsgPacked (context, ledgerPrefix, endorser) {
    return this.getMessageBytes(context, await this.createMsg(context, ledgerPrefix, endorser))
  }

  /**
     * Directs verity-application to start and create an issuer identity and set it up
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param ledgerPrefix a string indicating the location that the issuer identifier should be published to. Verity can publish to the following locations, indicated by the values in quotes:
     *                     [Sovrin Builder Net: "did:indy:sovrin:builder",
     *                     Sovrin Staging Net: "did:indy:sovrin:staging",
     *                     Sovrin Main Net: "did:indy:sovrin"]
     *                     The locations which are available to your Verity tenant will be configured based on your customer agreement.
     * @param endorser Optional: the desired endorser did. If left empty then Verity will attempt to use it's own endorser, otherwise it will return a transaction for manual endorsement
     */
  async create (context, ledgerPrefix, endorser = null) {
    await this.sendMessage(context, await this.createMsgPacked(context, ledgerPrefix, endorser))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #currentPublicIdentifier
     */
  async currentPublicIdentifierMsg (context) {
    let msg = this._getBaseMessage(this.msgNames.CURRENT_PUBLIC_IDENTIFIER)
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
