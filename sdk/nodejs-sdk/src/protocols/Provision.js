'use strict'
const URL = require('url').URL
const utils = require('../utils')
const Protocol = require('./Protocol')
const indy = require('../utils/indy')

module.exports = class Provision extends Protocol {
  constructor (threadId = null) {
    const msgFamily = 'agent-provisioning'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames = {
      CREATE_AGENT: 'CREATE_AGENT'
    }
  }

  async provisionSdkMsg (context) {
    [context.sdkPairwiseDID, context.sdkPairwiseVerkey] = await indy.newDid(context)
    return {
      '@id': this._getNewId(),
      '@type': this.getMessageType(this.msgNames.CREATE_AGENT),
      fromDID: context.sdkPairwiseDID,
      fromDIDVerKey: context.sdkPairwiseVerkey
    }
  }

  async provisionSdkMsgPacked (context) {
    return utils.packMessageForVerity(context, await this.provisionSdkMsg(context), true)
  }

  async provisionSdk (context) {
    const url = new URL(context.verityUrl)
    url.pathname = '/agency/msg'
    const rawResponse = await utils.httpPost(url.href, await this.provisionSdkMsgPacked(context))
    const response = utils.unpackMessage(rawResponse.body)
    context.verityPairwiseDID = response.verityPairwiseDID
    context.verityPairwiseVerkey = response.verityPairwiseVerkey
    return context
  }
}
