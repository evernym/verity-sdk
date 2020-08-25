'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
const GoalCodes = require('./GoalCodes')

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
   * @property {String} this.msgNames.CREATED - 'created'
   * @property {String} this.msgNames.INVITATION - 'invitation'
   * @property {String} this.goalCode - 'p2p-messaging'
   * @property {String} this.goal - 'To establish a peer-to-peer messaging relationship'
   */
  constructor (forRelationship = null, threadId = null, label = null, logoUrl = null) {
    const msgFamily = 'relationship'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.CONNECTION_INVITATION = 'connection-invitation'
    this.msgNames.OUT_OF_BAND_INVITATION = 'out-of-band-invitation'
    this.msgNames.CREATED = 'created'
    this.msgNames.INVITATION = 'invitation'

    this.forRelationship = forRelationship
    this.label = label
    this.logoUrl = logoUrl
    this.goalCode = 'p2p-messaging'
    this.goal = 'To establish a peer-to-peer messaging relationship'
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
    msg.label = this.label
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
     */
  async connectionInvitation (context, shortInvite = null) {
    await this.sendMessage(context, await this.connectionInvitationMsgPacked(context, shortInvite))
  }

  /**
   * Creates the control message without packaging and sending it.
   * @param context an instance of the Context object initialized to a verity-application agent
   * @return the constructed message (JSON object)
   *
   * @see #connectionInvitation
   */
  async outOfBandInvitationMsg (context, shortInvite = null, goal = GoalCodes.P2P_MESSAGING()) {
    var msg = this._getBaseMessage(this.msgNames.OUT_OF_BAND_INVITATION)
    msg.goalCode = this.goalCode
    msg.goal = this.goal
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
   * @return the byte array ready for transport
   *
   * @see #connectionInvitation
   */
  async outOfBandInvitationMsgPacked (context, shortInvite = null, goal = GoalCodes.P2P_MESSAGING()) {
    return this.getMessageBytes(context, await this.outOfBandInvitationMsg(context, shortInvite, goal))
  }

  /**
   * Ask for aries out of band invitation from the verity-application agent for the relationship created by this protocol
   *
   * @param context an instance of the Context object initialized to a verity-application agent
   */
  async outOfBandInvitation (context, shortInvite = null, goal = GoalCodes.P2P_MESSAGING()) {
    await this.sendMessage(context, await this.outOfBandInvitationMsgPacked(context, shortInvite, goal))
  }
}

module.exports = Relationship
