const Protocol = require('../Protocol')
const utils = require('../../utils')
/**
 * An interface for controlling a 1.0 IssueCredential protocol.
 * @extends Protocol
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/bb42a6c35e0d5543718fb36dd099551ab192f7b0/features/0036-issue-credential" target="_blank" rel="noopener noreferrer">Aries 0036: Issue Credential Protocol 1.0</a>
 */
class IssueCredentialV10 extends Protocol {
  /**
   * Creates an IssueCredentialV10 Protocol
   * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
   * @param threadId the thread id of the already started protocol
   * @param credDefId the Credential Definition that will be used to issue the credential
   * @param values a map of key-value pairs that make up the attributes in the credential
   * @param comment a human readable comment that is presented before issuing the credential
   * @param price token price (NOT CURRENTLY USED)
   * @param autoIssue flag to automatically issue the credential after receiving response from the receiver (skip getting
   *                  signal for the credential request and waiting for the issue control message)
   * @param byInvitation flag to create out-of-band invitation as a part of the IssueCredential protocol
   * @return 1.0 IssueCredential object
   *
   * @property {String} msgFamily - 'issue-credential'
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
   * @property {String} this.msgNames.PROTOCOL_INVITATION - 'protocol-invitation'
   */
  constructor (forRelationship,
    threadId = null,
    credDefId = '',
    values = {},
    comment = '',
    price = 0,
    autoIssue = false,
    byInvitation = false) {
    const msgFamily = 'issue-credential'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.credDefId = credDefId
    this.values = values
    this.comment = comment
    this.price = price
    this.autoIssue = autoIssue
    this.byInvitation = byInvitation
    this.created = threadId === null

    this.msgNames.OFFER_CREDENTIAL = 'offer'
    this.msgNames.PROPOSE_CREDENTIAL = 'proposal'
    this.msgNames.ISSUE_CREDENTIAL = 'issue'
    this.msgNames.REQUEST_CREDENTIAL = 'request'
    this.msgNames.REJECT_CREDENTIAL = 'reject'
    this.msgNames.CREDENTIAL_STATUS = 'status'
    this.msgNames.SENT = 'sent'
    this.msgNames.ACCEPT_REQUEST = 'accept-request'
    this.msgNames.PROTOCOL_INVITATION = 'protocol-invitation'
  }

  /**
     * Directs verity-application to send credential offer.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async offerCredential (context) {
    await this.sendMessage(context, await this.offerCredentialMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #offerCredential
     */
  async offerCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.offerCredentialMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #offerCredential
     */
  offerCredentialMsg () {
    if (!this.created) {
      throw new utils.WrongSetupError('Unable to offer credentials when NOT starting the interaction')
    }

    var msg = this._getBaseMessage(this.msgNames.OFFER_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg.price = this.price
    msg.credential_values = this.values
    msg.auto_issue = this.autoIssue
    msg.by_invitation = this.byInvitation
    msg = this._addThread(msg)

    return msg
  }

  /**
     * Directs verity-application to send credential proposal.
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async proposeCredential (context) {
    await this.sendMessage(context, await this.proposeCredentialMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #proposeCredential
     */
  async proposeCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.proposeCredentialMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #proposeCredential
     */
  proposeCredentialMsg () {
    if (!this.created) {
      throw new utils.WrongSetupError('Unable to propose credentials when NOT starting the interaction')
    }

    var msg = this._getBaseMessage(this.msgNames.PROPOSE_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg.credential_values = this.values
    msg = this._addThread(msg)

    return msg
  }

  /**
     * Directs verity-application to send credential request.
     *
     * @param context
     */
  async requestCredential (context) {
    await this.sendMessage(context, await this.requestCredentialMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #requestCredential
     */
  async requestCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.requestCredentialMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #requestCredential
     */
  requestCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.REQUEST_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg = this._addThread(msg)

    return msg
  }

  /**
     * Directs verity-application to issue credential and send it
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async issueCredential (context) {
    await this.sendMessage(context, await this.issueCredentialMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #issueCredential
     */
  async issueCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.issueCredentialMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #issueCredential
     */
  issueCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.ISSUE_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.comment = this.comment
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Directs verity-application to reject the credential protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async reject (context) {
    await this.sendMessage(context, await this.rejectMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #reject
     */
  async rejectMsgPacked (context) {
    return this.getMessageBytes(context, this.rejectMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #reject
     */
  rejectMsg () {
    var msg = this._getBaseMessage(this.msgNames.REJECT_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.comment = this.comment
    msg = this._addThread(msg)

    return msg
  }

  /**
     * Ask for status from the verity-application agent
     *
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
    var msg = this._getBaseMessage(this.msgNames.CREDENTIAL_STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }
}
module.exports = IssueCredentialV10
