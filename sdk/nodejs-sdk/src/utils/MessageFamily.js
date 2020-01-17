'use strict'
const uuid = require('uuid')
const utils = require('./index')

module.exports = class MessageFamily {
  constructor (msgFamily, msgFamilyVersion, msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER) {
    this.msgFamily = msgFamily
    this.msgFamilyVersion = msgFamilyVersion
    this.msgQualifier = msgQualifier
  }

  _getNewId () {
    return uuid()
  }

  _getBaseMessage (msgName) {
    const msg = {
      '@id': this._getNewId(),
      '@type': `${this.msgQualifier};spec/${this.msgFamily}/${this.msgFamilyVersion}/${msgName}`
    }
    return msg
  }

  getMessageType (messageName) {
    return `${this.msgQualifier};spec/${this.msgFamily}/${this.msgFamilyVersion}/${messageName}`
  }
}
