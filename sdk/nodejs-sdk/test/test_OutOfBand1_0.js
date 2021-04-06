/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const OutOfBand = require('../src/protocols/v1_0/OutOfBand')

const forRelationship = 'RxRJCMe5XNqc9e9J1YPwhL'
const inviteUrl = 'http://inviteUrl'

describe('OutOfBand', () => {
  it('should init correctly', async () => {
    const rel = new OutOfBand(
      forRelationship,
      inviteUrl
    )
    expect(rel.forRelationship).to.equal(forRelationship)
    expect(rel.inviteUrl).to.equal(inviteUrl)
  })

  it('should build REUSE msg correctly', async () => {
    const rel = new OutOfBand(
      forRelationship,
      inviteUrl
    )
    const msg = await rel.reuseMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.REUSE}`
    )
    expect(msg.inviteUrl).to.equal(inviteUrl)
  })
})
