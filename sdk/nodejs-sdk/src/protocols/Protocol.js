'use strict'
const uuid = require('uuid')
const utils = require('../utils')
const MessageFamily = require('../utils/MessageFamily')

module.exports = class Protocol extends MessageFamily {
  constructor (msgFamily, msgFamilyVersion, msgQualifier = null, threadId = null) {
    super(msgFamily, msgFamilyVersion, msgQualifier)
    if (threadId) {
      this.threadId = threadId
    } else {
      this.threadId = uuid()
    }
  }

  _addThread (msg) {
    msg['~thread'] = {
      thid: this.threadId
    }
    return msg
  }

  async getMessageBytes (context, message) {
    return utils.packMessageForVerity(context, message)
  }

  async sendMessage (context, message) {
    await utils.sendPackedMessage(context, message)
  }
}
