'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 1.0 CommittedAnswer protocol.
 */
module.exports = class CommittedAnswer extends Protocol {
  constructor (forRelationship, threadId = null, question = null, answerStr = null, descr = null, validResponses = null, signatureRequired = null) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'committedanswer'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '1.0'
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
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
