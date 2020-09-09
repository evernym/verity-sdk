/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
// const Context = require('../src/utils/Context')
const WriteSchema = require('../src/protocols/v0_6/WriteSchema')

const name = 'test label'
const version = '1.0'
const attributes = ['name', 'age']

describe('WriteSchema', () => {
  it('should init correctly', async () => {
    const rel = new WriteSchema(
      name,
      version,
      attributes
    )
    expect(rel.name).to.equal(name)
    expect(rel.version).to.equal(version)
    expect(rel.attrs).to.equal(attributes)
  })

  it('should build WRITE msg correctly', async () => {
    const rel = new WriteSchema(
      name,
      version,
      attributes
    )
    const msg = await rel.writeMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier};spec/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.WRITE_SCHEMA}`
    )
    expect(msg['~thread'].thid).to.be.a('string')
    expect(msg.name).to.equal(name)
    expect(msg.version).to.equal(version)
    expect(msg.attrNames).to.equal(attributes)
  })
})
