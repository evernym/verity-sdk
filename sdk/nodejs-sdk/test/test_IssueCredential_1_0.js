/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
const assert = chai.assert
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const IssueCredential = require('../src/protocols/v1_0/IssueCredential')

const forRelationship = 'some_did'
const threadId = 'some thread id'
const credDefId = '12345'
const credentialValues = {
  name: 'Joe',
  degree: 'Bachelors',
  gpa: '3.67'
}
const comment = 'degree'
const price = '5'
const autoIssue = true
const byInvitation = true

describe('IssueCredential', () => {
  it('should init correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      threadId,
      credDefId,
      credentialValues,
      comment,
      price,
      autoIssue,
      byInvitation
    )
    expect(issueCred.forRelationship).to.equal(forRelationship)
    expect(issueCred.threadId).to.equal(threadId)
    expect(issueCred.credDefId).to.equal(credDefId)
    expect(issueCred.values).to.equal(credentialValues)
    expect(issueCred.comment).to.equal(comment)
    expect(issueCred.price).to.equal(price)
    expect(issueCred.autoIssue).to.equal(autoIssue)
    expect(issueCred.byInvitation).to.equal(byInvitation)
  })

  it('should build propose msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      null,
      credDefId,
      credentialValues,
      comment
    )
    const msg = await issueCred.proposeCredentialMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.PROPOSE_CREDENTIAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    assert(msg['~thread'].thid)
    expect(msg.cred_def_id).to.equal(credDefId)
    expect(msg.credential_values).to.equal(credentialValues)
    expect(msg.comment).to.equal(comment)
  })

  it('should build offer msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      null,
      credDefId,
      credentialValues,
      comment,
      price,
      autoIssue,
      byInvitation
    )
    const msg = await issueCred.offerCredentialMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.OFFER_CREDENTIAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    assert(msg['~thread'].thid)
    expect(msg.cred_def_id).to.equal(credDefId)
    expect(msg.credential_values).to.equal(credentialValues)
    expect(msg.comment).to.equal(comment)
    expect(msg.price).to.equal(price)
    expect(msg.auto_issue).to.equal(autoIssue)
    expect(msg.by_invitation).to.equal(byInvitation)
  })

  it('should build request msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      threadId,
      credDefId,
      null,
      comment
    )
    const msg = await issueCred.requestCredentialMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.REQUEST_CREDENTIAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.cred_def_id).to.equal(credDefId)
    expect(msg.comment).to.equal(comment)
  })

  it('should build reject msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      threadId,
      null,
      null,
      comment
    )
    const msg = await issueCred.rejectMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.REJECT_CREDENTIAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.comment).to.equal(comment)
  })

  it('should build issue msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      threadId
    )
    const msg = await issueCred.issueCredentialMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.ISSUE_CREDENTIAL}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })

  it('should build status msg correctly', async () => {
    const issueCred = new IssueCredential(
      forRelationship,
      threadId
    )
    const msg = await issueCred.statusMsg()
    expect(msg['@type']).to.equal(
        `${issueCred.msgQualifier}/${issueCred.msgFamily}/${issueCred.msgFamilyVersion}/${issueCred.msgNames.CREDENTIAL_STATUS}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })
})
