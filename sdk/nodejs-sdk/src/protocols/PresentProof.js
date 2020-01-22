'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class PresentProof extends Protocol {
  constructor (forRelationship, threadId = null, name = null, attributes = null, predicates = null) {
    const msgFamily = 'present-proof'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.forRelationship = forRelationship
    this.name = name
    this.attributes = attributes
    this.predicates = predicates

    this.msgNames.PROOF_REQUEST = 'request'
    this.msgNames.GET_STATUS = 'get-status'
  }

  async requestMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.PROOF_REQUEST)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    msg.name = this.name
    msg.proofAttrs = this.attributes
    if (this.predicates) {
      msg.proofPredicates = this.predicates
    }

    return msg
  }

  async requestMsgPacked (context) {
    return this.getMessageBytes(context, await this.requestMsg(context))
  }

  async request (context) {
    await this.sendMessage(context, await this.requestMsgPacked(context))
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
