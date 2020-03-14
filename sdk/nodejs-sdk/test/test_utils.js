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
  [context.verityPublicDID, context.verityPublicVerkey] = await indy.newDid(context);
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
    }).timeout(5000)

    it('should be able to pack and unpack messages', async () => {
      const message = { some: 'message' }
      const context = await getTestContext()
      const bytes = await utils.packMessage(context.walletHandle, message, context.verityPairwiseDID, context.verityPairwiseVerkey, context.sdkPairwiseVerkey, context.verityPublicVerkey)
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
  describe('.truncateInviteDetailKeys', () => {
    it('should properly truncate invite detail keys', () => {
      const originalInviteDetails = { connReqId: 'NWM0OTJ', senderAgencyDetail: { DID: 'Noq4CGLi5Jhd1sky3fikmG', endpoint: '83b52483.ngrok.io:443/agency/msg', verKey: 'CtNixnkUgCj3rtbqsP6rnvsgaQKgraMfTkVXN4Rwndba' }, senderDetail: { DID: 'M1gWRDPMRpT4qfjuxicafB', agentKeyDlgProof: { agentDID: 'M1gWRDPMRpT4qfjuxicafB', agentDelegatedKey: 'BucRLSwVUASPMMCMxTHd1Lm5pwGtKpsEsM6bPm8D5Spu', signature: 'ys2VaC9BzZx8G1IHhGkmOF/Mvz49Qb+QjsDn5gx/yc5VXnGToPkhBtjLnP7Dfy83OjMTBAEAFIi238i1qLkGDw==' }, logoUrl: 'logo-url', name: 'user', verKey: 'BucRLSwVUASPMMCMxTHd1Lm5pwGtKpsEsM6bPm8D5Spu' }, statusCode: 'MS-101', statusMsg: 'message created', targetName: 'there', version: '2.0' }
      const shouldBeInviteDetails = { id: 'NWM0OTJ', sa: { d: 'Noq4CGLi5Jhd1sky3fikmG', e: '83b52483.ngrok.io:443/agency/msg', v: 'CtNixnkUgCj3rtbqsP6rnvsgaQKgraMfTkVXN4Rwndba' }, s: { d: 'M1gWRDPMRpT4qfjuxicafB', dp: { d: 'M1gWRDPMRpT4qfjuxicafB', k: 'BucRLSwVUASPMMCMxTHd1Lm5pwGtKpsEsM6bPm8D5Spu', s: 'ys2VaC9BzZx8G1IHhGkmOF/Mvz49Qb+QjsDn5gx/yc5VXnGToPkhBtjLnP7Dfy83OjMTBAEAFIi238i1qLkGDw==' }, l: 'logo-url', n: 'user', v: 'BucRLSwVUASPMMCMxTHd1Lm5pwGtKpsEsM6bPm8D5Spu' }, sc: 'MS-101', sm: 'message created', t: 'there', version: '2.0' }
      expect(JSON.parse(utils.truncateInviteDetailKeys(originalInviteDetails))).to.deep.equal(shouldBeInviteDetails)
    })
  })
})
