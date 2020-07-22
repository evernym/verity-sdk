'use strict'
const { v4: uuidv4 } = require('uuid')
const utils = require('../utils')
const MessageFamily = require('../utils/MessageFamily')

/**
 * The base class for all protocols
 * @extends MessageFamily
 */
class Protocol extends MessageFamily {
  /**
   * Creates a protocol
   * @param {String} msgFamily name of the protocol family.
   * @param {String} msgFamilyVersion protocol version used with specific message family.
   * @param {String} msgQualifier community qualifier.
   * @param {String} threadId given ID used for the thread.
   * @property {Object} this.msgNames - {STATUS: 'status-report', PROBLEM_REPORT: 'problem-report'}
   */
  constructor (msgFamily, msgFamilyVersion, msgQualifier = null, threadId = null) {
    super(msgFamily, msgFamilyVersion, msgQualifier)
    if (threadId) {
      this.threadId = threadId
    } else {
      this.threadId = uuidv4()
    }

    this.msgNames = {
      STATUS: 'status-report',
      PROBLEM_REPORT: 'problem-report'
    }
  }

  /**
   * Attaches the thread block (including the thid) for a protocol to the given message object (JSON)
   *
   * @param msg with the thread block attached
   */
  _addThread (msg) {
    msg['~thread'] = {
      thid: this.threadId
    }
    return msg
  }

  /**
   * Packs the connection message for the verity-application
   * @param context an instance of Context that has been initialized with your wallet and key details
   * @param message the message to be packed for the verity-application
   * @return Encrypted connection message ready to be sent to the verity
   */
  async getMessageBytes (context, message) {
    return utils.packMessageForVerity(context, message)
  }

  /**
   * Encrypts and sends a specified message to Verity
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param message the message to send to Verity
   */
  async sendMessage (context, message) {
    await utils.sendPackedMessage(context, message)
  }
}
module.exports = Protocol
