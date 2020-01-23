'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class CommittedAnswer extends Protocol {
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
  }

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

  async askMsgPacked (context) {
    return this.getMessageBytes(context, await this.askMsg(context))
  }

  async ask (context) {
    await this.sendMessage(context, await this.askMsgPacked(context))
  }

  async answerMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.ANSWER_QUESTION)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    msg.response = this.answerStr
    return msg
  }

  async answerMsgPacked (context) {
    return this.getMessageBytes(context, await this.answerMsg(context))
  }

  async answer (context) {
    await this.sendMessage(context, await this.answerMsgPacked(context))
  }

  async statusMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    return msg
  }

  async statusMsgPacked (context) {
    return this.getMessageBytes(context, await this.statusMsg(context))
  }

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }
}
