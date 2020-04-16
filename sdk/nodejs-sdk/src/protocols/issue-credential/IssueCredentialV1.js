const Protocol = require('../Protocol')
const utils = require('../utils')

module.exports = class IssueCredentialV1 extends Protocol {
  constructor (forRelationship,
    credDefId = '',
    values = {},
    comment = '',
    price = 0,
    threadId = null) {
    const msgFamily = 'issue-credential'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.credDefId = credDefId
    this.values = values
    this.comment = comment
    this.price = price
    // this.created = true

    this.addMsgNames()
  }

  addMsgNames () {
    this.msgNames.OFFER_CREDENTIAL = 'offer-credential'
    this.msgNames.PROPOSE_CREDENTIAL = 'propose-credential'
    this.msgNames.ISSUE_CREDENTIAL = 'issue-credential'
    this.msgNames.REQUEST_CREDENTIAL = 'request-credential'
  }

  async offerCredential (context) {
    await this.sendMessage(context, await this.offerCredentialMsgPacked(context))
  }

  async offerCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.offerCredentialMsg())
  }

  offerCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.OFFER_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg.price = this.price
    msg.values = this.values
    msg = this._addThread(msg)
    return msg
  }

  async proposeCredential (context) {
    await this.sendMessage(context, await this.proposeCredentialMsgPacked(context))
  }

  async proposeCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.proposeCredentialMsg())
  }

  proposeCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.PROPOSE_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg.values = this.values
    msg = this._addThread(msg)
    return msg
  }

  async requestCredential (context) {
    await this.sendMessage(context, await this.requestCredentialMsgPacked(context))
  }

  async requestCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.requestCredentialMsg())
  }

  requestCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.REQUEST_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.cred_def_id = this.credDefId
    msg.comment = this.comment
    msg = this._addThread(msg)
    return msg
  }

  async issueCredential (context) {
    await this.sendMessage(context, await this.issueCredentialMsgPacked(context))
  }

  async issueCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.issueCredentialMsg())
  }

  issueCredentialMsg () {
    var msg = this._getBaseMessage(this.msgNames.ISSUE_CREDENTIAL)
    // not implemented
    return msg
  }
}
