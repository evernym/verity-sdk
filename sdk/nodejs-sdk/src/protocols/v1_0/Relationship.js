'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 1.0 Relationship protocol.
 * @extends Protocol
 */
class Relationship extends Protocol {
  /**
   * Constructor for the 1.0 Relationship object. This constructor creates an object that is ready to create a new
   * relationship.
   * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
   * @param threadId the thread id of the already started protocol
   * @param label the label presented in the invitation to connect to this relationship
   * @param logoUrl logo url presented in invitation
   * @return 1.0 Relationship object
   *
   * @property {String} msgFamily - 'relationship'
   * @property {String} msgFamilyVersion - '1.0'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.CREATE - 'create'
   * @property {String} this.msgNames.CONNECTION_INVITATION - 'connection-invitation'
   * @property {String} this.msgNames.OUT_OF_BAND_INVITATION - 'out-of-band-invitation'
   * @property {String} this.msgNames.SMS_CONNECTION_INVITATION - 'sms-connection-invitation'
   * @property {String} this.msgNames.SMS_OUT_OF_BAND_INVITATION - 'sms-out-of-band-invitation'
   * @property {String} this.msgNames.CREATED - 'created'
   * @property {String} this.msgNames.INVITATION - 'invitation'
   */
  constructor (forRelationship = null, threadId = null, label = null, logoUrl = null) {
    const msgFamily = 'relationship'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.CONNECTION_INVITATION = 'connection-invitation'
    this.msgNames.OUT_OF_BAND_INVITATION = 'out-of-band-invitation'
    this.msgNames.SMS_CONNECTION_INVITATION = 'sms-connection-invitation'
    this.msgNames.SMS_OUT_OF_BAND_INVITATION = 'sms-out-of-band-invitation'
    this.msgNames.CREATED = 'created'
    this.msgNames.INVITATION = 'invitation'

    this.forRelationship = forRelationship
    this.label = label
    this.logoUrl = logoUrl
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
    if (this.label) {
      msg.label = this.label
    }
    if (this.logoUrl) {
      msg.logoUrl = this.logoUrl
    }
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
     * Directs verity-application to create a new relationship
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite enables short version of invite to be generated
     * @return the constructed message (JSON object)
     *
     * @see #connectionInvitation
     */
  async connectionInvitationMsg (context, shortInvite = null) {
    var msg = this._getBaseMessage(this.msgNames.CONNECTION_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    if (shortInvite != null) {
      msg.shortInvite = shortInvite
    }
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite enables short version of invite to be generated
     * @return the byte array ready for transport
     *
     * @see #connectionInvitation
     */
  async connectionInvitationMsgPacked (context, shortInvite = null) {
    return this.getMessageBytes(context, await this.connectionInvitationMsg(context, shortInvite))
  }

  /**
     * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param shortInvite enables short version of invite to be generated
     */
  async connectionInvitation (context, shortInvite = null) {
    await this.sendMessage(context, await this.connectionInvitationMsgPacked(context, shortInvite))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
     * @return the constructed message (JSON object)
     *
     * @see #smsConnectionInvitation
     */
  async smsConnectionInvitationMsg (context, phoneNumber) {
    var msg = this._getBaseMessage(this.msgNames.SMS_CONNECTION_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg.phoneNumber = phoneNumber
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
     * @return the byte array ready for transport
     *
     * @see #smsConnectionInvitation
     */
  async smsConnectionInvitationMsgPacked (context, phoneNumber) {
    return this.getMessageBytes(context, await this.smsConnectionInvitationMsg(context, phoneNumber))
  }

  /**
     * Ask for aries invitation from the verity-application agent for the relationship created by this protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
     */
  async smsConnectionInvitation (context, phoneNumber) {
    await this.sendMessage(context, await this.smsConnectionInvitationMsgPacked(context, phoneNumber))
  }

  /**
   * Creates the control message without packaging and sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param shortInvite enables short version of invite to be generated
   * @param goal goal code for providing the goal of the connection
   * @return the constructed message (JSON object)
   *
   * @see #outOfBandInvitation
   */
  async outOfBandInvitationMsg (context, shortInvite = null, goal = null) {
    var msg = this._getBaseMessage(this.msgNames.OUT_OF_BAND_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    if (shortInvite != null) {
      msg.shortInvite = shortInvite
    }

    if (goal != null) {
      msg.goalCode = goal.code
      msg.goal = goal.goalName
    }
    return msg
  }

  /**
   * Creates and packages message without sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param shortInvite enables short version of invite to be generated
   * @param goal goal code for providing the goal of the connection
   * @return the byte array ready for transport
   *
   * @see #outOfBandInvitation
   */
  async outOfBandInvitationMsgPacked (context, shortInvite = null, goal = null) {
    return this.getMessageBytes(context, await this.outOfBandInvitationMsg(context, shortInvite, goal))
  }

  /**
   * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
   *
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param shortInvite enables short version of invite to be generated
   * @param goal goal code for providing the goal of the connection
   */
  async outOfBandInvitation (context, shortInvite = null, goal = null) {
    await this.sendMessage(context, await this.outOfBandInvitationMsgPacked(context, shortInvite, goal))
  }

  /**
   * Creates the control message without packaging and sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
   * @param goal goal code for providing the goal of the connection
   * @return the constructed message (JSON object)
   *
   * @see #smsOutOfBandInvitation
   */
  async smsOutOfBandInvitationMsg (context, phoneNumber, goal = null) {
    var msg = this._getBaseMessage(this.msgNames.SMS_OUT_OF_BAND_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg.phoneNumber = phoneNumber
    msg = this._addThread(msg)

    if (goal != null) {
      msg.goalCode = goal.code
      msg.goal = goal.goalName
    }
    return msg
  }

  /**
   * Creates and packages message without sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
   * @param goal goal code for providing the goal of the connection
   * @return the byte array ready for transport
   *
   * @see #smsOutOfBandInvitation
   */
  async smsOutOfBandInvitationMsgPacked (context, phoneNumber, goal) {
    return this.getMessageBytes(context, await this.smsOutOfBandInvitationMsg(context, phoneNumber, goal))
  }

  /**
   * Ask for SMS aries out of band invitation from the verity-application agent for the relationship created by this protocol
   *
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param phoneNumber mobile phone number in international format (eg. +18011234567) which can be used for sending SMS invitations.
   * @param goal goal code for providing the goal of the connection
   */
  async smsOutOfBandInvitation (context, phoneNumber, goal = null) {
    await this.sendMessage(context, await this.smsOutOfBandInvitationMsgPacked(context, phoneNumber, goal))
  }
}

module.exports = Relationship
