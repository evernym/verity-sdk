'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 WriteSchema protocol.
 */
module.exports = class WriteSchema extends Protocol {
  constructor (name, version, attrs, threadId = null) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'write-schema'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '0.6'
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.name = name
    this.version = version
    this.attrs = attrs

    /**
     Name for 'write' control message
     */
    this.msgNames.WRITE_SCHEMA = 'write'
    this.statuses = {}
    this.statuses.WRITE_SUCCESSFUL = 0
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #write
     */
  async writeMsg (context) {
    const msg = this._getBaseMessage(this.msgNames.WRITE_SCHEMA)
    msg.name = this.name
    msg.version = this.version
    msg.attrNames = this.attrs
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #write
     */
  async writeMsgPacked (context) {
    return this.getMessageBytes(context, await this.writeMsg(context))
  }

  /**
     * Directs verity-application to write the specified Schema to the Ledger
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async write (context) {
    await this.sendMessage(context, await this.writeMsgPacked(context))
  }
}
