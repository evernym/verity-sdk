'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

module.exports = class WriteSchema extends Protocol {
  constructor (name, version, attrs, threadId = null) {
    const msgFamily = 'write-schema'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.name = name
    this.version = version
    this.attrs = attrs

    this.msgNames.WRITE_SCHEMA = 'write'
    this.statuses = {}
    this.statuses.WRITE_SUCCESSFUL = 0
  }

  async writeMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.WRITE_SCHEMA)
    msg.name = this.name
    msg.version = this.version
    msg.attrNames = this.attrs
    return msg
  }

  async writeMsgPacked (context) {
    return this.getMessageBytes(context, await this.writeMsg(context))
  }

  async write (context) {
    await this.sendMessage(context, await this.writeMsgPacked(context))
  }
}
