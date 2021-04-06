/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const BasicMessage = require('../src/protocols/v1_0/BasicMessage')

const forRelationship = 'some_did'

const content = 'hello, world'
const sentTime = '2018-12-13T17:29:34+0000'
const localization = 'en'

describe('BasicMessage', () => {
  it('should init correctly', async () => {
    const basicMessage = new BasicMessage(
      forRelationship,
      null,
      content,
      sentTime,
      localization
    )
    expect(basicMessage.forRelationship).to.equal(forRelationship)
    expect(basicMessage.sentTime).to.equal(sentTime)
    expect(basicMessage.content).to.equal(content)
    expect(basicMessage.localization).to.equal(localization)
  })

  it('should build propose msg correctly', async () => {
    const basicMessage = new BasicMessage(
      forRelationship,
      null,
      content,
      sentTime,
      localization
    )
    const msg = await basicMessage.messageMsg()
    expect(msg['@type']).to.equal(
        `${basicMessage.msgQualifier}/${basicMessage.msgFamily}/${basicMessage.msgFamilyVersion}/${basicMessage.msgNames.BASIC_MESSAGE}`
    )
    expect(msg['~for_relationship']).to.equal(forRelationship)
    expect(msg.content).to.equal(content)
    expect(msg.sent_time).to.equal(sentTime)
    expect(msg['~l10n'].locale).to.equal(localization)
  })
})
