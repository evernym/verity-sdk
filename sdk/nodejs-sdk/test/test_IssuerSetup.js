/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
 const Context = require('../src/utils/Context')
const Relationship = require('../src/protocols/v0_7/IssuerSetup')

const threadId = '7a80285e-896c-45f6-b386-39ed7c49230c'

describe('IssuerSetup', () => {

  it('should build CREATE msg correctly', async () => {
    const configStr = getTestConfig()
    const context = await Context.createWithConfig(configStr)
    const issuerSetup = new IssuerSetup()
    const msg = await issuerSetup.createMsg(context, "did:indy:sovrin:builder")
    expect(msg['@type']).to.equal(
     `${issuerSetup.msgQualifier}/${issuerSetup.msgFamily}/${issuerSetup.msgFamilyVersion}/${issuerSetup.msgNames.CREATE}`
    )
    expect(msg['~thread'].thid).to.equal(threadId)
  })

  it('should build CREATE msg correctly with endorser', async () => {
    const configStr = getTestConfig()
    const context = await Context.createWithConfig(configStr)
    const issuerSetup = new IssuerSetup()
    const msg = await issuerSetup.createMsg(context, "did:indy:sovrin:builder", "someEndorser")
    expect(msg['@type']).to.equal(
     `${issuerSetup.msgQualifier}/${issuerSetup.msgFamily}/${issuerSetup.msgFamilyVersion}/${issuerSetup.msgNames.CREATE}`
    )
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.endorser).to.equal("someEndorser")
  })
})
