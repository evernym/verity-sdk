/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const Relationship = require('../src/protocols/v1_0/Relationship')

describe('Relationship', () => {
  it('should init correctly', async () => {
    const rel = new Relationship(
      'RxRJCMe5XNqc9e9J1YPwhL',
      '7a80285e-896c-45f6-b386-39ed7c49230c',
      'test'
    )
    expect(rel.threadId).to.equal('7a80285e-896c-45f6-b386-39ed7c49230c')
    expect(rel.forRelationship).to.equal('RxRJCMe5XNqc9e9J1YPwhL')
    expect(rel.label).to.equal('test')
  })

  it('should build CREATE msg correctly', async () => {
    const rel = new Relationship(
      'RxRJCMe5XNqc9e9J1YPwhL',
      '7a80285e-896c-45f6-b386-39ed7c49230c',
      'test'
    )
    const msg = await rel.createMsg(null)
    expect(msg.label).to.equal('test')
  })

  it('should build OutOfBand invitation msg correctly', async () => {
    const rel = new Relationship(
      'RxRJCMe5XNqc9e9J1YPwhL',
      '7a80285e-896c-45f6-b386-39ed7c49230c',
      null,
      'testGoalCode',
      'testGoal',
      'testRequestAttach'
    )
    const msg = await rel.outOfBandInvitationMsg(null)
    expect(msg.goalCode).to.equal('testGoalCode')
    expect(msg.goal).to.equal('testGoal')
    expect(msg['request~attach']).to.equal('testRequestAttach')
  })
})
