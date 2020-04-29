const Protocol = require('../Protocol')
const utils = require('../utils')

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
    this.msgNames.GET_STATUS = 'get-status'
  }

  async request (context) {
    await this.sendMessage(context, await this.requestMsgPacked(context))
  }

  async requestMsgPacked (context) {
    await this.getMessageBytes(context, this.requestMsg())
  }

  requestMsg () {
    var msg = this._getBaseMessage(this.msgNames.PROOF_REQUEST)
    msg['~for_relationship'] = this.forRelationship
    msg.name = this.name
    if (this.attributes) {
      msg.proofAttrs = this.attributes
    }
    if (this.predicates) {
      msg.proofPredicates = this.predicates
    }
    msg = this._addThread(msg)
    return msg
  }

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }

  async statusMsgPacked (context) {
    await this.getMessageBytes(context, this.statusMsg())
  }

  statusMsg () {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }
}
