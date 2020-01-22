'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class IssueCredential extends Protocol {
  constructor (forRelationship, threadId = null, name = null, values = null, credDefId = null) {
    const msgFamily = 'issue-credential'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.forRelationship = forRelationship
    this.name = name
    this.values = values
    this.credDefId = credDefId
    this.price = '0'

    this.msgNames = {
      OFFER_CREDENTIAL: 'send-offer',
      ISSUE_CREDENTIAL: 'issue-credential',
      GET_STATUS: 'get-status'
    }
  }

  async offerCredentialMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.OFFER_CREDENTIAL)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    msg.name = this.name
    msg.credDefId = this.credDefId
    msg.credentialValues = this.values
    msg.price = this.price
    return msg
  }

  async offerCredentialMsgPacked (context) {
    return this.getMessageBytes(context, await this.offerCredentialMsg(context))
  }

  async offerCredential (context) {
    await this.sendMessage(context, await this.offerCredentialMsgPacked(context))
  }

  async issueCredentialMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.ISSUE_CREDENTIAL)
    msg = this._addThread(msg)
    msg['~for_relationship'] = this.forRelationship
    return msg
  }

  async issueCredentialMsgPacked (context) {
    return this.getMessageBytes(context, await this.issueCredentialMsg(context))
  }

  async issueCredential (context) {
    await this.sendMessage(context, await this.issueCredentialMsgPacked(context))
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
