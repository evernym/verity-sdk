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
    expect(msg.requesterVk).to.equal(ctx.sdkVerKey)

    const testToken = '{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp": "2020-06-05T21:33:36.085Z", "sig": "ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey": "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"}'
    const p2 = new Provision(null, testToken)
    const msg2 = p2.provisionMsg(ctx)
    expect(msg2.provisionToken)
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

  it('should validate token', async () => {
    const p = new Provision()

    const testToken = '{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp": "2020-06-05T21:33:36.085Z", "sig": "ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey": "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"}'
    await p.validateToken(JSON.parse(testToken))

    const testToken2 = '{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp": "2020-06-05T21:33:36.085Z", "sig": "AkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey": "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"}'
    try {
      await p.validateToken(JSON.parse(testToken2))
      expect(false)
    } catch (e) {}
  }).timeout(5000)
})
