/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const Relationship = require('../src/protocols/v1_0/Relationship')
const GoalCodes = require('../src/protocols/v1_0/GoalCodes')

const forRelationship = 'RxRJCMe5XNqc9e9J1YPwhL'
const threadId = '7a80285e-896c-45f6-b386-39ed7c49230c'
const label = 'test label'
const logoUrl = 'http://test.logo/url'
const phoneNumber = '+18011234567'
const shortInvite = true

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

  it('should build CREATE msg correctly', async () => {
    const rel = new Relationship(
      null,
      threadId,
      label
    )
    const msg = await rel.createMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CREATE}`
    )
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.label).to.equal(label)
    expect(msg.logoUrl).to.an('undefined')
  })

  it('should build CREATE msg correctly with logo url', async () => {
    const rel = new Relationship(
      null,
      threadId,
      label,
      logoUrl
    )
    const msg = await rel.createMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CREATE}`
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
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CONNECTION_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
  })
  it('should build INVITATION msg with shortInvite correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.connectionInvitationMsg(null, shortInvite)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.CONNECTION_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.shortInvite).to.equal(shortInvite)
  })
  it('should build SMS INVITATION msg correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.smsConnectionInvitationMsg(null, phoneNumber)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.SMS_CONNECTION_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.phoneNumber).to.equal(phoneNumber)
  })
  it('should build OutOfBand invitation msg correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.outOfBandInvitationMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.OUT_OF_BAND_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.goalCode).to.an('undefined')
    expect(msg.goal).to.an('undefined')
  })
  it('should build OutOfBand invitation msg correctly with goal', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.outOfBandInvitationMsg(null, null, GoalCodes.ISSUE_VC())
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.OUT_OF_BAND_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.goalCode).to.equal(GoalCodes.ISSUE_VC().code)
    expect(msg.goal).to.equal(GoalCodes.ISSUE_VC().goalName)
  })
  it('should build OutOfBand invitation msg with shortInvite correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.outOfBandInvitationMsg(null, shortInvite)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.OUT_OF_BAND_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.goalCode).to.an('undefined')
    expect(msg.goal).to.an('undefined')
    expect(msg.shortInvite).to.equal(shortInvite)
  })
  it('should build SMS OutOfBand invitation msg correctly', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.smsOutOfBandInvitationMsg(null, phoneNumber)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.SMS_OUT_OF_BAND_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.phoneNumber).to.equal(phoneNumber)
    expect(msg.goalCode).to.an('undefined')
    expect(msg.goal).to.an('undefined')
  })
  it('should build SMS OutOfBand invitation msg correctly with goal', async () => {
    const rel = new Relationship(
      forRelationship,
      threadId
    )
    const msg = await rel.smsOutOfBandInvitationMsg(null, phoneNumber, GoalCodes.ISSUE_VC())
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.SMS_OUT_OF_BAND_INVITATION}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg['~thread'].thid).to.equal(threadId)
    expect(msg.phoneNumber).to.equal(phoneNumber)
    expect(msg.goalCode).to.equal(GoalCodes.ISSUE_VC().code)
    expect(msg.goal).to.equal(GoalCodes.ISSUE_VC().goalName)
  })
})
