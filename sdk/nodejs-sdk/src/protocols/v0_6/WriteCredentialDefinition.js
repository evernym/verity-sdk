'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 WriteCredentialDefinition protocol.
 */
module.exports = class WriteCredentialDefinition extends Protocol {
  constructor (name, schemaId, tag = null, revocation = null, threadId = null) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'write-cred-def'
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
    this.schemaId = schemaId
    this.tag = tag
    this.revocation = revocation

    /**
     Name for 'write' control message
     */
    this.msgNames.WRITE_CRED_DEF = 'write'
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #write
     */
  async writeMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.WRITE_CRED_DEF)
    msg.name = this.name
    msg.schemaId = this.schemaId
    msg.tag = this.tag
    msg.revocationDetails = this.revocation
    msg = this._addThread(msg)
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
     * Directs verity-application to write the specified Credential Definition to the Ledger
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async write (context) {
    await this.sendMessage(context, await this.writeMsgPacked(context))
  }
}
