'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

module.exports = class Relationship extends Protocol {
  constructor (forRelationship = null, threadId = null, label = null, logoUrl = null, goalCode = null, goal = null, request = null) {
    const msgFamily = 'relationship'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.CREATE = 'create'
    this.msgNames.CONNECTION_INVITATION = 'connection-invitation'
    this.msgNames.OUT_OF_BAND_INVITATION = 'out-of-band-invitation'
    this.msgNames.CREATED = 'created'
    this.msgNames.INVITATION = 'invitation'

    this.forRelationship = forRelationship
    this.label = label
    this.logoUrl = logoUrl
    this.goalCode = goalCode
    this.goal = goal
    this.request = request
  }

  async createMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CREATE)
    msg = this._addThread(msg)
    msg.label = this.label
    if (this.logoUrl) {
      msg.logoUrl = this.logoUrl
    }
    return msg
  }

  async createMsgPacked (context) {
    return this.getMessageBytes(context, await this.createMsg(context))
  }

  async create (context) {
    await this.sendMessage(context, await this.createMsgPacked(context))
  }

  async connectionInvitationMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.CONNECTION_INVITATION)
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  async connectionInvitationMsgPacked (context) {
    return this.getMessageBytes(context, await this.connectionInvitationMsg(context))
  }

  async connectionInvitation (context) {
    await this.sendMessage(context, await this.connectionInvitationMsgPacked(context))
  }

  async outOfBandInvitationMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.OUT_OF_BAND_INVITATION)
    msg.goalCode = this.goalCode
    msg.goal = this.goal
    if (this.request) {
      msg['request~attach'] = this.request
    }
    msg['~for_relationship'] = this.forRelationship
    msg = this._addThread(msg)
    return msg
  }

  async outOfBandInvitationMsgPacked (context) {
    return this.getMessageBytes(context, await this.outOfBandInvitationMsg(context))
  }

  async outOfBandInvitation (context) {
    await this.sendMessage(context, await this.outOfBandInvitationMsgPacked(context))
  }
}
