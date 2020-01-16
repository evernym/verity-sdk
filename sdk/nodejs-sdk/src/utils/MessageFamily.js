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
    return {
      '@id': this._getNewId(),
      '@type': `${this.msgQualifier};spec/${this.msgFamily}/${this.msgFamilyVersion}/${msgName}`
    }
  }

  getMessageType (messageName) {
    return `${this.msgQualifier};spec/${this.msgFamily}/${this.msgFamilyVersion}/${messageName}`
  }
}
