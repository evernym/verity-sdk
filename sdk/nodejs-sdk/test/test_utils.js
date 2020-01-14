'use strict'
const chai = require('chai')
const expect = chai.expect
const sdk = require('indy-sdk')
const utils = require('../src/utils')
const Context = require('../src/Context')
const getTestConfig = require('./test_Context').getTestConfig

async function getTestContext() {
  const context = await Context.create(getTestConfig());
  [context.verityPairwiseDID, context.verityPairwiseVerkey] = await sdk.createAndStoreMyDid(context.walletHandle, {});
  [context.sdkPairwiseDID, context.sdkPairwiseVerkey] = await sdk.createAndStoreMyDid(context.walletHandle, {});
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
      const bytes = await utils.packMessageForVerity(context, message)
      expect((await utils.unpackMessageFromVerity(context, bytes)).message).to.deep.equal(message)
      context.deleteWallet()
    })
  })
})
