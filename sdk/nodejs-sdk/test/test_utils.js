/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
const utils = require('../src/utils')
const indy = require('../src/utils/indy')
const Context = require('../src/utils/Context')
const getTestConfig = require('./test_Context').getTestConfig

async function getTestContext () {
  const context = await Context.createWithConfig(getTestConfig());
  [context.verityPairwiseDID, context.verityPairwiseVerkey] = await indy.newDid(context);
  [context.sdkPairwiseDID, context.sdkPairwiseVerkey] = await indy.newDid(context)
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
    })

    it('should be able to pack and unpack messages', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const bytes = await utils.packMessage(context, message)
      expect((await utils.unpackMessage(context, bytes)).message).to.deep.equal(message)
      context.deleteWallet()
    })

    it('should be able to pack and unpack forward messages', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const forwardMessage = await utils.packMessageForVerity(context, message)
      const packedMessage = await utils.unpackMessage(context, forwardMessage)
      expect((await utils.unpackMessage(context, packedMessage)).message).to.deep.equal(message)
      context.deleteWallet()
    })
  })
})
