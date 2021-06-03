const Protocol = require('../Protocol')
const utils = require('../../utils')
/**
 * An interface for controlling a 1.0 PresentProof protocol.
 * @extends Protocol
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof" target="_blank" rel="noopener noreferrer">Aries 0037: Present Proof Protocol 1.0</a>
 */
class PresentProofV1x0 extends Protocol {
  /**
  * Constructor for the 1.0 PresentProof object. This constructor creates an object that is ready to start
  * process of requesting a presentation of proof
  *
  * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
  * @param threadId the thread id of the already started protocol
  * @param name A human readable name for the given request
  * @param attributes An array of attribute based restrictions
  * @param predicates An array of predicates based restrictions (">=", ">", "<=", "<")
  * @param byInvitation flag to create out-of-band invitation as a part of the PresentProof protocol
  * @return 1.0 PresentProof object
  *
  * @property {String} msgFamily - 'present-proof'
  * @property {String} msgFamilyVersion - '1.0'
  * @property {String} msgQualifier - 'Community Qualifier'
  * @property {String} this.msgNames.PROOF_REQUEST - 'request'
  * @property {String} this.msgNames.PRESENTATION_RESULT - 'presentation-result'
  * @property {String} this.msgNames.STATUS - 'status'
  * @property {String} this.msgNames.REJECT - 'reject'
  * @property {String} this.msgNames.PROTOCOL_INVITATION - 'protocol-invitation'
  */
  constructor (forRelationship,
    threadId = null,
    name = '',
    attributes = [],
    predicates = [],
    byInvitation = false
  ) {
    const msgFamily = 'present-proof'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.name = name
    this.attributes = attributes
    this.predicates = predicates
    this.byInvitation = byInvitation
    this.created = threadId == null

    this.msgNames.PROOF_REQUEST = 'request'
    this.msgNames.PRESENTATION_RESULT = 'presentation-result'
    this.msgNames.STATUS = 'status'
    this.msgNames.REJECT = 'reject'
    this.msgNames.ACCEPT_PROPOSAL = 'accept-proposal'
    this.msgNames.PROTOCOL_INVITATION = 'protocol-invitation'
  }

  /**
     * Directs verity-application to request a presentation of proof.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async request (context) {
    await this.sendMessage(context, await this.requestMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #request
     */
  async requestMsgPacked (context) {
    return this.getMessageBytes(context, this.requestMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #request
     */
  requestMsg () {
    if (!this.created) {
      throw new utils.WrongSetupError('Unable to request presentation when NOT starting the interaction')
    }
    var msg = this._getBaseMessage(this.msgNames.PROOF_REQUEST)
    msg['~for_relationship'] = this.forRelationship
    msg.name = this.name
    if (this.attributes) {
      msg.proof_attrs = this.attributes
    }
    if (this.predicates && this.predicates.length > 0) {
      msg.proof_predicates = this.predicates
    }
    msg.by_invitation = this.byInvitation
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
    var msg = this._getBaseMessage(this.msgNames.STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Directs verity-application to accept the proposal for presenting the proof.
     * verity-application will send presentation request based on the proposal.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param reason an human readable reason for the rejection
     */
  async acceptProposal (context) {
    await this.sendMessage(context, await this.acceptProposalMsgPacked(context))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #acceptProposal
     */
  async acceptProposalMsgPacked (context) {
    return this.getMessageBytes(context, this.acceptProposalMsg())
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #reject
     */
  acceptProposalMsg () {
    var msg = this._getBaseMessage(this.msgNames.ACCEPT_PROPOSAL)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  /**
     * Directs verity-application to reject this presentation proof protocol
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @param reason an human readable reason for the rejection
     */
  async reject (context, reason) {
    await this.sendMessage(context, await this.rejectMsgPacked(context, reason))
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #reject
     */
  async rejectMsgPacked (context, reason) {
    return this.getMessageBytes(context, this.rejectMsg(reason))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #reject
     */
  rejectMsg (reason) {
    var msg = this._getBaseMessage(this.msgNames.REJECT)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    if (reason) {
      msg.reason = reason
    }
    return msg
  }
}
module.exports = PresentProofV1x0
