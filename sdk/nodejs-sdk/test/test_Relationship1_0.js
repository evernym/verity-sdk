/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const Relationship = require('../src/protocols/v1_0/Relationship')

const forRelationship = 'RxRJCMe5XNqc9e9J1YPwhL'
const threadId = '7a80285e-896c-45f6-b386-39ed7c49230c'
const label = 'test label'
const logoUrl = 'http://test.logo/url'

describe('Relationship', () => {
  it('should init correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId,
      label,
      logoUrl
    )
    expect(rel.threadId).to.equal(threadId)
    expect(rel.forRelationship).to.equal(forRelationship)
    expect(rel.label).to.equal(label)
    expect(rel.logoUrl).to.equal(logoUrl)
  })

  it('should build CREATE msg correctly without profile url', async () => {
    const rel = new Relationship(
      null,
      threadId,
      label
    )
    const msg = await rel.createMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier};spec/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CREATE}`
    )
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.label).to.equal(label)
    expect(msg.logoUrl).to.an('undefined')
  })

  it('should build CREATE msg correctly with profile url', async () => {
    const rel = new Relationship(
      null,
      threadId,
      label,
      logoUrl
    )
    const msg = await rel.createMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier};spec/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CREATE}`
    )
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.label).to.equal(label)
    expect(msg.logoUrl).to.equal(logoUrl)
  })

  it('should build INVITATION msg correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.connectionInvitationMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier};spec/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CONNECTION_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })
})
