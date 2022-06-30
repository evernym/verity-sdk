/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
const IssuerSetup = require('../src/protocols/v0_7/IssuerSetup')

describe('IssuerSetup', () => {
  it('should build CREATE msg correctly', async () => {
    const issuerSetup = new IssuerSetup()
    const msg = await issuerSetup.createMsg(null, 'did:indy:sovrin:builder')
    expect(msg['@type']).to.equal(
     `${issuerSetup.msgQualifier}/${issuerSetup.msgFamily}/${issuerSetup.msgFamilyVersion}/${issuerSetup.msgNames.CREATE}`
    )
  })

  it('should build CREATE msg correctly with endorser', async () => {
    const issuerSetup = new IssuerSetup()
    const msg = await issuerSetup.createMsg(null, 'did:indy:sovrin:builder', 'someEndorser')
    expect(msg['@type']).to.equal(
     `${issuerSetup.msgQualifier}/${issuerSetup.msgFamily}/${issuerSetup.msgFamilyVersion}/${issuerSetup.msgNames.CREATE}`
    )
    expect(msg.endorser).to.equal('someEndorser')
  })
})
