'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 WriteCredentialDefinition protocol.
 * @extends Protocol
 */
class WriteCredentialDefinition extends Protocol {
  /**
   * Constructor for the 0.6 WriteCredentialDefinition object. This constructor creates an object that is
   * ready to write a credential definitions the ledger
   *
   * @param name The name of the new credential definition
   * @param schemaId The id of the schema this credential definition will be based on
   * @param tag An optional tag for the credential definition
   * @param revocation the revocation object defining revocation support.
   * @param threadId the thread id of the already started protocol
   * @return 0.6 WriteCredentialDefinition object
   *
   * @property {String} msgFamily - 'write-cred-def'
   * @property {String} msgFamilyVersion - '0.6'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.WRITE - 'write'
   */
  constructor (name, schemaId, tag = null, revocation = null, threadId = null) {
    const msgFamily = 'write-cred-def'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)
    this.name = name
    this.schemaId = schemaId
    this.tag = tag
    this.revocation = revocation

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
module.exports = WriteCredentialDefinition
