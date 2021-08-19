/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const UpdateEndpoint = require('../src/protocols/v0_6/UpdateEndpoint')
const helpers = require('../test/test_utils')

const authentication = {
  type: 'OAuth2',
  version: 'v1',
  data: {
    url: 'https://auth.url/token',
    grant_type: 'client_credentials',
    client_id: 'ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA',
    client_secret: 'aaGxxcGi6kb6AxIe'
  }
}

describe('UpdateEndpoint', () => {
  it('should init correctly', async () => {
    const ue = new UpdateEndpoint(
      authentication
    )
    expect(ue.authentication).to.equal(authentication)
  })

  it('should build UPDATE_COM_METHOD msg correctly', async () => {
    const context = await helpers.getTestContext()
    const ue = new UpdateEndpoint()
    const msg = await ue.updateMsg(context)
    expect(msg['@id']).to.not.be.an('undefined')
    expect(msg['@type']).to.equal(
     `${ue.msgQualifier}/${ue.msgFamily}/${ue.msgFamilyVersion}/${ue.msgNames.UPDATE_ENDPOINT}`
    )
    expect(msg.comMethod.value).to.equal(context.endpointUrl)
    expect(msg.comMethod.id).to.equal('webhook')
    expect(msg.comMethod.type).to.equal(2)
    expect(msg.comMethod.packaging.pkgType).to.equal('1.0')
    expect(msg.comMethod.packaging.recipientKeys).to.eql([context.sdkVerKey])
    expect(msg.comMethod.authentication).to.an('undefined')
  }).timeout(5000)

  it('should build UPDATE_COM_METHOD msg with OAuth2 v1 authentication correctly', async () => {
    const context = await helpers.getTestContext()
    const ue = new UpdateEndpoint(
      {
        type: 'OAuth2',
        version: 'v1',
        data: {
          url: 'https://auth.url/token',
          grant_type: 'client_credentials',
          client_id: 'ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA',
          client_secret: 'aaGxxcGi6kb6AxIe'
        }
      }
    )
    const msg = await ue.updateMsg(context)
    expect(msg['@id']).to.not.be.an('undefined')
    expect(msg['@type']).to.equal(
     `${ue.msgQualifier}/${ue.msgFamily}/${ue.msgFamilyVersion}/${ue.msgNames.UPDATE_ENDPOINT}`
    )
    expect(msg.comMethod.value).to.equal(context.endpointUrl)
    expect(msg.comMethod.id).to.equal('webhook')
    expect(msg.comMethod.type).to.equal(2)
    expect(msg.comMethod.packaging.pkgType).to.equal('1.0')
    expect(msg.comMethod.packaging.recipientKeys).to.eql([context.sdkVerKey])

    // authentication
    expect(msg.comMethod.authentication.type).to.equal('OAuth2')
    expect(msg.comMethod.authentication.version).to.equal('v1')
    expect(msg.comMethod.authentication.data.url).to.equal('https://auth.url/token')
    expect(msg.comMethod.authentication.data.grant_type).to.equal('client_credentials')
    expect(msg.comMethod.authentication.data.client_id).to.equal('ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA')
    expect(msg.comMethod.authentication.data.client_secret).to.equal('aaGxxcGi6kb6AxIe')
  }).timeout(5000)
})
