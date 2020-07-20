const Protocol = require('../Protocol')
const utils = require('../../utils')
/**
 * An interface for controlling a 1.0 Connections protocol.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0160-connection-protocol" target="_blank" rel="noopener noreferrer">Aries 0160: Connection Protocol</a>
 */
module.exports = class ConnectingV10 extends Protocol {
  constructor (label, base64InviteURL, threadId = null) {
    /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    const msgFamily = 'connections'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '1.0'
     /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.label = label
    this.base64InviteURL = base64InviteURL

    this.msgNames.SEND_ACCEPT_INVITE = 'accept'
    this.msgNames.GET_STATUS = 'status'
    this.msgNames.REQUEST_RECEIVED = 'request-received'
    this.msgNames.RESPONSE_SENT = 'response-sent'
  }

  /**
     * Accepts connection defined by the given invitation
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async accept (context) {
    await this.sendMessage(context, await this.acceptMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #accept
     */
  async acceptMsgPascked (context) {
    return this.getMessageBytes(context, this.acceptMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #accept
     */
  acceptMsg () {
    var msg = this._getBaseMessage(this.msgNames.SEND_ACCEPT_INVITE)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    msg.label = this.label
    msg.invite_url = this.base64InviteURL
    return msg
  }

  /**
   * Sends the get status message to the connection
   * @param context an instance of the Context object initialized to a verity-application agent
   */
  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #status
     */
  async statusMsgPacked (context) {
    return this.getMessageBytes(context, this.rejectMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #status
     */
  statusMsg () {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }
}
