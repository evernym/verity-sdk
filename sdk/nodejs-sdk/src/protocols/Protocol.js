'use strict'
const { v4: uuidv4 } = require('uuid')
const utils = require('../utils')
const MessageFamily = require('../utils/MessageFamily')

module.exports = class Protocol extends MessageFamily {
  constructor (msgFamily, msgFamilyVersion, msgQualifier = null, threadId = null) {
    super(msgFamily, msgFamilyVersion, msgQualifier)
    if (threadId) {
      this.threadId = threadId
    } else {
      this.threadId = uuidv4()
    }

    this.msgNames = {
      STATUS: 'status-report',
      PROBLEM_REPORT: 'problem-report'
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
