const Protocol = require('../Protocol')
const utils = require('../../utils')
/**
 * An interface for controlling a 1.0 Connections protocol.
 * @extends Protocol
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0160-connection-protocol" target="_blank" rel="noopener noreferrer">Aries 0160: Connection Protocol</a>
 */
class ConnectingV10 extends Protocol {
  /**
   * Constructor for the 1.0 Connections object
   * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
   * @param label A human readable string that will label the caller identity (often an organization).
   *              E.g. 'Acme Corp`
   * @param base64InviteURL the invitation URL as specified by the Aries 0160: Connection Protocol (eg. https://<domain>/<path>?c_i=<invitation-string>)
   * @param threadId The thread id of the already started protocol
   * @return 1.0 Connections object*
   *
   * @property {String} msgFamily - 'connections'
   * @property {String} msgFamilyVersion - '1.0'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.OFFER_CREDENTIAL - 'offer'
   * @property {String} this.msgNames.PROPOSE_CREDENTIAL - 'proposal'
   * @property {String} this.msgNames.ISSUE_CREDENTIAL - 'issue'
   * @property {String} this.msgNames.REQUEST_CREDENTIAL - 'request'
   * @property {String} this.msgNames.REJECT_CREDENTIAL - 'reject'
   * @property {String} this.msgNames.CREDENTIAL_STATUS - 'status'
   * @property {String} this.msgNames.SENT - 'sent'
   * @property {String} this.msgNames.ACCEPT_REQUEST - 'accept-request'
   */
  constructor (forRelationship, label, base64InviteURL, threadId = null) {
    const msgFamily = 'connections'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
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
  async acceptMsgPacked (context) {
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
    return this.getMessageBytes(context, this.statusMsg())
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
module.exports = ConnectingV10
