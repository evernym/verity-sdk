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

function createWriteSchemaTest (name, version, attributes) {
  const rel = new WriteSchema(
    name,
    version,
    attributes
  )
  expect(rel.name).to.equal(name)
  expect(rel.version).to.equal(version)
  expect(rel.attrs).to.equal(attributes)

  return rel
}

describe('WriteSchema', () => {
  it('should init correctly', async () => {
    createWriteSchemaTest(name, version, attributes)

    expect(function () { createWriteSchemaTest(null, version, attributes) }).to.throw('requirement failed: required that name must NOT be null or empty')
    expect(function () { createWriteSchemaTest('', version, attributes) }).to.throw('requirement failed: required that name must NOT be null or empty')
    expect(function () { createWriteSchemaTest(name, null, attributes) }).to.throw('requirement failed: required that version must NOT be null or empty')
    expect(function () { createWriteSchemaTest(name, '', attributes) }).to.throw('requirement failed: required that version must NOT be null or empty')
    expect(function () { createWriteSchemaTest(name, version, null) }).to.throw('requirement failed: required that attrs must NOT be null')
    expect(function () { createWriteSchemaTest(name, version, ['name', null]) }).to.throw('requirement failed: required that elements of attrs must NOT be null')
  })

  it('should build WRITE msg correctly', async () => {
    const rel = createWriteSchemaTest(
      name,
      version,
      attributes
    )
    const msg = await rel.writeMsg(null)
    expect(msg['@type']).to.equal(
     `${rel.msgQualifier}/${rel.msgFamily}/${rel.msgFamilyVersion}/${rel.msgNames.WRITE_SCHEMA}`
    )
    expect(msg['~thread'].thid).to.be.a('string')
    expect(msg.name).to.equal(name)
    expect(msg.version).to.equal(version)
    expect(msg.attrNames).to.equal(attributes)
  })
})
