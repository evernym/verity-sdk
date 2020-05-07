const Protocol = require('../Protocol')
const utils = require('../../utils')

module.exports = class PresentProofV1x0 extends Protocol {
  constructor (forRelationship,
    threadId = null,
    name = '',
    attributes = [],
    predicates = []
  ) {
    const msgFamily = 'present-proof'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.name = name
    this.attributes = attributes
    this.predicates = predicates
    this.created = threadId == null

    this.msgNames.PROOF_REQUEST = 'request'
    this.msgNames.STATUS = 'status'
    this.msgNames.REJECT = 'reject'
  }

  async request (context) {
    await this.sendMessage(context, await this.requestMsgPacked(context))
  }

  async requestMsgPacked (context) {
    return this.getMessageBytes(context, this.requestMsg())
  }

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

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }

  async statusMsgPacked (context) {
    return this.getMessageBytes(context, this.statusMsg())
  }

  statusMsg () {
    var msg = this._getBaseMessage(this.msgNames.STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  async reject (context, reason) {
    await this.sendMessage(context, await this.rejectMsgPacked(context, reason))
  }

  async rejectMsgPacked (context, reason) {
    return this.getMessageBytes(context, this.rejectMsgPacked(reason))
  }

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
