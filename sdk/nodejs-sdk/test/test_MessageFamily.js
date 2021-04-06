/* eslint-env mocha */
'use strict'
const chai = require('chai')
const expect = chai.expect
chai.use(require('chai-as-promised'))
const MessageFamily = require('../src/utils/MessageFamily')
const utils = require('../src/utils/index')
const qualType = utils.constants.USE_NEW_QUALIFIER_FORMAT

describe('MessageFamily', () => {
  it('should correctly parse a message type', () => {
    const messageType = MessageFamily.parseMessageType(qualType ? 'https://didcomm.evernym.com/configs/0.6/UPDATE_COM_METHOD' : 'did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD')
    expect(messageType.qualifier).to.equal(utils.constants.EVERNYM_MSG_QUALIFIER)
    expect(messageType.msgFamily).to.equal('configs')
    expect(messageType.msgFamilyVersion).to.equal('0.6')
    expect(messageType.msgName).to.equal('UPDATE_COM_METHOD')
  })
})
