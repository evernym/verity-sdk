const Protocol = require('../Protocol')
const utils = require('../../utils')

module.exports = class IssueCredentialV10 extends Protocol {
  constructor (forRelationship,
    threadId = null,
    credDefId = '',
    values = {},
    comment = '',
    price = 0) {
    const msgFamily = 'issue-credential'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.forRelationship = forRelationship
    this.credDefId = credDefId
    this.values = values
    this.comment = comment
    this.price = price
    this.created = threadId === null

    this.msgNames.OFFER_CREDENTIAL = 'offer'
    this.msgNames.PROPOSE_CREDENTIAL = 'proposal'
    this.msgNames.ISSUE_CREDENTIAL = 'issue'
    this.msgNames.REQUEST_CREDENTIAL = 'request'
    this.msgNames.REJECT_CREDENTIAL = 'reject'
    this.msgNames.CREDENTIAL_STATUS = 'status'
    this.msgNames.SENT = 'sent'
    this.msgNames.ACCEPT_REQUEST = 'accept-request'

  }

  async offerCredential (context) {
    await this.sendMessage(context, await this.offerCredentialMsgPacked(context))
  }

  async offerCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.offerCredentialMsg())
  }

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

  async requestCredential (context) {
    await this.sendMessage(context, await this.requestCredentialMsgPacked(context))
  }

  async requestCredentialMsgPacked (context) {
    return this.getMessageBytes(context, this.requestCredentialMsg())
  }

  requestCredentialMsg () {
    if (!this.created) {
      throw new utils.WrongSetupError('Unable to request credential when NOT starting the interaction')
    }

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
    msg['~for_relationship'] = this.forRelationship
    msg.comment = this.comment
    msg = this._addThread(msg)
    return msg
  }

  async reject (context) {
    await this.sendMessage(context, await this.rejectMsgPacked(context))
  }

  async rejectMsgPacked (context) {
    return this.getMessageBytes(context, this.rejectMsg())
  }

  rejectMsg () {
    if (!this.created) {
      throw new utils.WrongSetupError('Unable to reject when NOT starting the interaction')
    }

    var msg = this._getBaseMessage(this.msgNames.REJECT_CREDENTIAL)
    msg['~for_relationship'] = this.forRelationship
    msg.comment = this.comment
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
    var msg = this._getBaseMessage(this.msgNames.CREDENTIAL_STATUS)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }
}
