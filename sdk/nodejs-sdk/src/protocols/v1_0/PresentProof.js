const Protocol = require('../Protocol')
const utils = require('../../utils')

/**
 * An interface for controlling a 1.0 PresentProof protocol.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof" target="_blank" rel="noopener noreferrer">Aries 0037: Present Proof Protocol 1.0</a>
 */
module.exports = class PresentProofV1x0 extends Protocol {
  constructor (forRelationship,
    threadId = null,
    name = '',
    attributes = [],
    predicates = []
  ) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'present-proof'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '1.0'
    /**
     * The qualifier for the message family. Uses the community qualifier.
     */
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.name = name
    this.attributes = attributes
    this.predicates = predicates
    this.created = threadId == null

    this.msgNames.PROOF_REQUEST = 'request'
    this.msgNames.PRESENTATION_RESULT = 'presentation-result'
    this.msgNames.STATUS = 'status'
    this.msgNames.REJECT = 'reject'
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
    return this.getMessageBytes(context, this.rejectMsgPacked(reason))
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
