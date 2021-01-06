'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/**
 * 1.0 BasicMessage protocol
 * The BasicMessage protocol allows one self-sovereign party send another self-sovereign party a message.
 * Support for this protocol is EXPERIMENTAL.
 * This protocol is not implemented in the Connect.Me app and the only way it can be used is by building the mobile app using mSDK or using some other Aries compatible wallet that supports BasicMessage
 * @extends Protocol
 */
class BasicMessage extends Protocol {
  /**
   * Constructor for the 1.0 BasicMessage object. This constructor creates an object that is ready to send
   * the given message
   *
   * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
   * @param threadId the thread id of the already started protocol
   * @param content The main text of the message
   * @param sentTime The time the message was sent
   * @param localization Language localization code
   * @return 1.0 BasicMessage object
   *
   * @property {String} msgFamily - 'basicmessage'
   * @property {String} msgFamilyVersion - '1.0'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.BASIC_MESSAGE - 'send-message'
   */
  constructor (forRelationship, threadId = null, content = null, sentTime = null, localization = null) {
    const msgFamily = 'basicmessage'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.forRelationship = forRelationship
    this.content = content
    this.sentTime = sentTime
    this.localization = localization

    this.msgNames.BASIC_MESSAGE = 'send-message'
  }

  /**
     * Creates the control message without packaging and sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #message
     */
  async messageMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.BASIC_MESSAGE)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)

    msg.content = this.content
    msg.sent_time = this.sentTime
    msg['~l10n'] = { locale: this.localization }
    return msg
  }

  /**
     * Creates and packages message without sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #message
     */
  async messageMsgPacked (context) {
    return this.getMessageBytes(context, await this.messageMsg(context))
  }

  /**
     * Directs verity-application to send the message
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async message (context) {
    await this.sendMessage(context, await this.messageMsgPacked(context))
  }
}
module.exports = BasicMessage
