/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
const Context = require('../src/utils/Context')
const utils = require('../src/utils')

const testConfig = {
  verityUrl: 'http://vas-team1.pdev.evernym.com',
  verityPublicDID: '3pjVfGmNysjiS5FiGPaa3F',
  verityPublicVerkey: '2YEuuFaKV3gvbuCUVKGwWxXdiPtHvocV2VNJ7LK9knn1',
  verityPairwiseDID: 'LQYTvDVJpUF76aqBoPnq2p',
  verityPairwiseVerkey: 'BaTcmxFB6SdXh2xMhjc8thzh4P37jNbvx8QVHXeN2b5R',
  sdkPairwiseDID: '5wEFMRrGhJUpXegY1eYxdK',
  sdkPairwiseVerkey: '3h1FAFLNLaqyknzzE9xBrchQQC1XkLERWZuyr3ACWyy9',
  endpointUrl: 'http://localhost:4002',
  walletName: utils.miniId(),
  walletKey: '12345'
}

function getTestConfig () {
  const config = JSON.parse(JSON.stringify(testConfig)) // Copy object
  config.walletName = utils.miniId()
  return JSON.stringify(config)
}

exports.getTestConfig = getTestConfig

describe('Context', () => {
  it('should accept valid configuration and contain all data', async () => {
    let config = getTestConfig()
    const context = await Context.createWithConfig(config)
    config = JSON.parse(config)
    expect(context.verityUrl).to.equal(config.verityUrl)
    expect(context.verityPublicDID).to.equal(config.verityPublicDID)
    expect(context.verityPublicVerkey).to.equal(config.verityPublicVerkey)
    expect(context.verityPairwiseDID).to.equal(config.verityPairwiseDID)
    expect(context.verityPairwiseVerkey).to.equal(config.verityPairwiseVerkey)
    expect(context.sdkPairwiseDID).to.equal(config.sdkPairwiseDID)
    expect(context.sdkPairwiseVerkey).to.equal(config.sdkPairwiseVerkey)
    expect(context.endpointUrl).to.equal(config.endpointUrl)
    expect(context.walletConfig).to.equal(JSON.stringify({ id: config.walletName }))
    expect(context.walletCredentials).to.equal(JSON.stringify({ key: config.walletKey }))
    expect(context.walletHandle).to.be.a('number')
    await context.deleteWallet()
  }).timeout(5000)

  it('should parse config if a JSON value', async () => {
    let config = getTestConfig()
    const context = await Context.createWithConfig(JSON.parse(config))
    config = JSON.parse(config)
    expect(context.verityUrl).to.equal(config.verityUrl)
    expect(context.verityPublicDID).to.equal(config.verityPublicDID)
    expect(context.verityPublicVerkey).to.equal(config.verityPublicVerkey)
    expect(context.verityPairwiseDID).to.equal(config.verityPairwiseDID)
    expect(context.verityPairwiseVerkey).to.equal(config.verityPairwiseVerkey)
    expect(context.sdkPairwiseDID).to.equal(config.sdkPairwiseDID)
    expect(context.sdkPairwiseVerkey).to.equal(config.sdkPairwiseVerkey)
    expect(context.endpointUrl).to.equal(config.endpointUrl)
    expect(context.walletConfig).to.equal(JSON.stringify({ id: config.walletName }))
    expect(context.walletCredentials).to.equal(JSON.stringify({ key: config.walletKey }))
    expect(context.walletHandle).to.be.a('number')
    await context.deleteWallet()
  }).timeout(5000)

  it('should reject invalid configuration', async () => {
    const requiredAttributes = [
      'verityUrl',
      'verityPublicDID',
      'verityPublicVerkey',
      'sdkPairwiseDID',
      'sdkPairwiseVerkey',
      'walletName',
      'walletKey'
    ]
    for (const key of requiredAttributes) {
      const config = JSON.parse(getTestConfig())
      delete config[key]
      await expect(Context.createWithConfig(JSON.stringify(config))).to.be.rejectedWith(Error, 'Invalid Context Configuration: missing attribute "' + key + '"')
    }
  })

  it('should throw an error if configuration is passed into the Context constructor', () => {
    // eslint-disable-next-line no-new
    expect(() => { new Context(getTestConfig()) }).to.throw(Error, 'Invalid arguments to Context constructor. Context should be created with Context.create or Context.createWithConfig.')
  })
})
