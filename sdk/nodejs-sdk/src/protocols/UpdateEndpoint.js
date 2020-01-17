'use strict'
const utils = require('../utils')
const Protocol = require('./Protocol')

const COM_METHOD_TYPE = 2

module.exports = class UpdateEndpoint extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'configs'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames = {
      UPDATE_ENDPOINT: 'UPDATE_COM_METHOD'
    }
  }

  async updateMsg (context) {
    if (context.endpointUrl === null || context.endpointUrl === '') {
      throw Error('Unable to update endpoint because context.endpointUrl is not defined')
    }
    const msg = this._getBaseMessage(this.msgNames.UPDATE_ENDPOINT)
    msg.comMethod = {
      id: 'webhook',
      type: COM_METHOD_TYPE,
      value: context.endpointUrl
    }
  }

  async updateMsgPacked (context) {
    return this.getMessageBytes(context, await this.updateMsg())
  }

  async update (context) {
    await this.sendMessage(context, await this.updateMsgPacked())
  }
}
