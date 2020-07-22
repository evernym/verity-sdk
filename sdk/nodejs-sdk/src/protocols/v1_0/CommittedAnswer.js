'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/**
 * 1.0 CommittedAnswer protocol
 * The CommittedAnswer protocol allows one self-sovereign party ask another self-sovereign a question and get a answer.
 * The answer can be signed (not required) and must be one of a specified list of responses
 * @extends Protocol
 */
class CommittedAnswer extends Protocol {
  /**
   * Constructor for the 1.0 CommittedAnswer object. This constructor creates an object that is ready to ask
   * the given question
   *
   * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
   * @param threadId the thread id of the already started protocol
   * @param question The main text of the question (included in the message the Connect.Me user signs with their private key)
   * @param answerStr the given answer from the list of valid responses given in the question
   * @param descr Any additional information about the question
   * @param validResponses The given possible responses.
   * @param signatureRequired if a signature must be included with the answer
   * @return 1.0 CommittedAnswer object
   *
   * @property {String} msgFamily - 'committedanswer'
   * @property {String} msgFamilyVersion - '1.0'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.ASK_QUESTION - 'ask-question'
   * @property {String} this.msgNames.ANSWER_QUESTION - 'answer-question'
   * @property {String} this.msgNames.GET_STATUS - 'get-status'
   * @property {String} this.msgNames.ANSWER_GIVEN - 'answer-given'
   */
  constructor (forRelationship, threadId = null, question = null, answerStr = null, descr = null, validResponses = null, signatureRequired = null) {
    const msgFamily = 'committedanswer'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.forRelationship = forRelationship
    this.question = question
    this.answerStr = answerStr
    this.descr = descr
    this.validResponses = validResponses
    if (signatureRequired) {
      this.signatureRequired = signatureRequired
    } else {
      this.signatureRequired = true
    }

    this.msgNames.ASK_QUESTION = 'ask-question'
    this.msgNames.ANSWER_QUESTION = 'answer-question'
    this.msgNames.GET_STATUS = 'get-status'
    this.msgNames.ANSWER_GIVEN = 'answer-given'
  }

  /**
     * Creates the control message without packaging and sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #ask
     */
  async askMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.ASK_QUESTION)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    msg.text = this.question
    msg.detail = this.descr
    msg.valid_responses = this.validResponses
    msg.signature_required = this.signatureRequired
    return msg
  }

  /**
     * Creates and packages message without sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #ask
     */
  async askMsgPacked (context) {
    return this.getMessageBytes(context, await this.askMsg(context))
  }

  /**
     * Directs verity-application to ask the question
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async ask (context) {
    await this.sendMessage(context, await this.askMsgPacked(context))
  }

  /**
     * Creates the control message without packaging and sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #answer
     */
  async answerMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.ANSWER_QUESTION)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    msg.response = this.answerStr
    return msg
  }

  /**
     * Creates and packages message without sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #answer
     */
  async answerMsgPacked (context) {
    return this.getMessageBytes(context, await this.answerMsg(context))
  }

  /**
     * Directs verity-application to answer the question
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async answer (context) {
    await this.sendMessage(context, await this.answerMsgPacked(context))
  }

  /**
     * Creates the control message without packaging and sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #status
     */
  async statusMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    return msg
  }

  /**
     * Creates and packages message without sending it.
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #status
     */
  async statusMsgPacked (context) {
    return this.getMessageBytes(context, await this.statusMsg(context))
  }

  /**
     * Ask for status from the verity-application agent
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }
}
module.exports = CommittedAnswer
