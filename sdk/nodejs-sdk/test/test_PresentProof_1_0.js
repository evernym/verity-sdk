/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
const assert = chai.assert
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const PresentProof = require('../src/protocols/v1_0/PresentProof')

const issuerDID = 'issuerDID'
const forRelationship = 'some_did'
const threadId = 'some thread id'
const name = 'some name'
const attributes = [
  {
    name: 'name',
    restrictions: [{ issuer_did: issuerDID }]
  },
  {
    name: 'degree',
    restrictions: [{ issuer_did: issuerDID }]
  },
  {
    names: ['first_name', 'last_name'],
    restrictions: [{ issuer_did: issuerDID }]
  }
]
const predicates = [
  {
    name: 'age',
    p_type: '>=',
    p_value: 18,
    restrictions: [{ issuer_did: issuerDID }]
  }
]
const rejectReason = 'because'

describe('PresentProof', () => {
  it('should init correctly', async () => {
    const presentProof = new PresentProof(
      forRelationship,
      threadId,
      name,
      attributes,
      predicates
    )
    expect(presentProof.forRelationship).to.equal(forRelationship)
    expect(presentProof.threadId).to.equal(threadId)
    expect(presentProof.name).to.equal(name)
    expect(presentProof.attributes).to.equal(attributes)
    expect(presentProof.predicates).to.equal(predicates)
  })

  it('should build request msg correctly', async () => {
    const presentProof = new PresentProof(
      forRelationship,
      null,
      name,
      attributes,
      predicates
    )
    const msg = await presentProof.requestMsg()
    expect(msg['@type']).to.equal(
        `${presentProof.msgQualifier}/${presentProof.msgFamily}/${presentProof.msgFamilyVersion}/${presentProof.msgNames.PROOF_REQUEST}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    assert(msg['~thread'].thid)
    expect(msg.name).to.equal(name)
    expect(msg.proof_attrs).to.equal(attributes)
    expect(msg.proof_predicates).to.equal(predicates)
  })

  it('should build status msg correctly', async () => {
    const presentProof = new PresentProof(
      forRelationship,
      threadId
    )
    const msg = await presentProof.statusMsg()
    expect(msg['@type']).to.equal(
        `${presentProof.msgQualifier}/${presentProof.msgFamily}/${presentProof.msgFamilyVersion}/${presentProof.msgNames.STATUS}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })

  it('should build accept proposal msg correctly', async () => {
    const presentProof = new PresentProof(
      forRelationship,
      threadId
    )
    const msg = await presentProof.acceptProposalMsg()
    expect(msg['@type']).to.equal(
        `${presentProof.msgQualifier}/${presentProof.msgFamily}/${presentProof.msgFamilyVersion}/${presentProof.msgNames.ACCEPT_PROPOSAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })

  it('should build reject msg correctly', async () => {
    const presentProof = new PresentProof(
      forRelationship,
      threadId
    )
    const msg = await presentProof.rejectMsg(rejectReason)
    expect(msg['@type']).to.equal(
        `${presentProof.msgQualifier}/${presentProof.msgFamily}/${presentProof.msgFamilyVersion}/${presentProof.msgNames.REJECT}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.reason).to.equal(rejectReason)
  })
})
