/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const Provision = require('../src/protocols/v0_7/Provision')
const helpers = require('../test/test_utils')

describe('Provision', () => {
  it('should gen message correctly', async () => {
    const ctx = await helpers.getTestContext()
    const p = new Provision()
    const msg = p.provisionMsg(ctx)
    expect(msg.requesterKeys.fromVerKey).to.equal(ctx.sdkVerKey)
  }).timeout(5000)

  it('should build CREATE msg correctly', async () => {
    const ctx = await helpers.getTestContext()
    const p = new Provision()
    p.sendToVerity = async function (context, packedMsg) {
      return {
        selfDID: '4ut8tgCBdUMCYZrJh5JS4o',
        agentVerKey: '38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE'
      }
    }
    const ctx2 = await p.provision(ctx)
    expect(ctx2.domainDID).to.equal('4ut8tgCBdUMCYZrJh5JS4o')
    expect(ctx2.verityAgentVerKey).to.equal('38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE')
  }).timeout(5000)
})
