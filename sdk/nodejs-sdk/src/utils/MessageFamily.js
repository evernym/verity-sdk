'use strict'
const { v4: uuidv4 } = require('uuid')
const utils = require('./index')

/**
 * Interface for message families. Message families are the set of messages used by a protocol. They include
 * three types of messages: protocol messages, control messages and signal messages.
 * <p/>
 * Protocol messages are messages exchange between parties of the protocol. Each party is an independent self-sovereign
 * domain.
 * <p/>
 * Control messages are messages sent by a controller (applications that use verity-sdk are controllers) to the verity
 * application. These messages control the protocol and make decisions for the protocol.
 * <p/>
 * Signal messages are messages sent from the verity-application agent to a controller
 * <p/>
 * Message family messages always have a type. This type has 4 parts: qualifier, family, version and name. Three parts
 * are static and defined by this interface.
 * <p/>
 * Example: did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/problem_report
 * <p/>
 * qualifier: did:sov:BzCbsNYhMrjHiqZDTUASHg
 * <br/>
 * family: connections
 * <br/>
 * version: 1.0
 * <br/>
 * name: problem_report
 *
 */
class MessageFamily {
  /**
   * MessageFamily Constructor
   * @param {String} msgFamily - the family name for the message family
   * @param {String} msgFamilyVersion - the version for the message family
   * @param {String} msgQualifier - the qualifier for the message family
   */
  constructor (msgFamily, msgFamilyVersion, msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER) {
    this.msgFamily = msgFamily
    this.msgFamilyVersion = msgFamilyVersion
    this.msgQualifier = msgQualifier
  }

  _getNewId () {
    return uuidv4()
  }

  _getBaseMessage (msgName) {
    const msg = {
      '@id': this._getNewId(),
      '@type': this._getMessageType(msgName)
    }
    return msg
  }

  _getMessageType (messageName) {
    return `${this.msgQualifier}/${this.msgFamily}/${this.msgFamilyVersion}/${messageName}`
  }

  /**
   * Extracts the fully qualified message type into fields - (qualifier, msgFamily, msgFamilyVersion, msgName)
   * @param messageType a fully qualified message type
   * @return {Object} parsed message type
   */
  static parseMessageType (messageType) {
    const result = {}
    const msgTypeParts = messageType.split('/')
    if (utils.constants.USE_NEW_QUALIFIER_FORMAT) {
      result.qualifier = msgTypeParts[0].concat('//').concat(msgTypeParts[2])
      result.msgFamily = msgTypeParts[3]
      result.msgFamilyVersion = msgTypeParts[4]
      result.msgName = msgTypeParts[5]
    } else {
      result.qualifier = msgTypeParts[0]
      result.msgFamily = msgTypeParts[1]
      result.msgFamilyVersion = msgTypeParts[2]
      result.msgName = msgTypeParts[3]
    }
    return result
  }
}
module.exports = MessageFamily
