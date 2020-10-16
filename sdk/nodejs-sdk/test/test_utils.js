/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
const utils = require('../src/utils')
const indy = require('../src/utils/indy')
const Context = require('../src/utils/Context')
const getTestConfig = require('./test_Context').getTestConfig
const buildWalletConfig = require('./test_Context').buildWalletConfig
const buildWalletCredentails = require('./test_Context').buildWalletCredentails

module.exports.getTestContext = getTestContext

async function getTestContext () {
  const configStr = getTestConfig()
  const config = JSON.parse(configStr)
  await indy.createWallet(buildWalletConfig(config.walletName, config.walletPath), buildWalletCredentails(config.walletKey))
  const context = await Context.createWithConfig(configStr);
  [context.verityPublicDID, context.verityPublicVerKey] = await indy.newDid(context);
  [context.domainDID, context.verityAgentVerKey] = await indy.newDid(context);
  [context.sdkVerKeyId, context.sdkVerKey] = await indy.newDid(context)
  return context
}

describe('utils', () => {
  describe('.packMessageForVerity', () => {
    it('should be able to pack messages', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const bytes = await utils.packMessageForVerity(context, message)
      expect(bytes)
      context.deleteWallet()
    }).timeout(5000)

    it('should be able to pack and unpack messages', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const bytes = await utils.packMessage(context.walletHandle, message, context.domainDID, context.verityAgentVerKey, context.sdkVerKey, context.verityPublicVerKey)
      expect((await utils.unpackForwardMessage(context, bytes)).message).to.deep.equal(message)
      context.deleteWallet()
    }).timeout(5000)

    it('should be able to pack and unpack messages for verity', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const forwardMessage = await utils.packMessageForVerity(context, message)
      const wrappedMessage = await utils.unpackForwardMessage(context, forwardMessage)
      expect(wrappedMessage.message).to.deep.equal(message)
      context.deleteWallet()
    }).timeout(5000)
  })
})
