'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 WriteSchema protocol.
 * @extends Protocol
 */
class WriteSchema extends Protocol {
  /**
   * Constructor for the 0.6 WriteSchema object. This constructor creates an object that is ready to write a schema
   * the ledger.
   *
   * @param name The given name for the schema
   * @param version The given version of the schema
   * @param attrs A varargs list of attribute name in the schema
   * @param threadId the thread id of the already started protocol
   * @return 0.6 WriteSchema object
   *
   * @property {String} msgFamily - 'write'
   * @property {String} msgFamilyVersion - '0.6'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.WRITE_SCHEMA - 'write'
   */
  constructor (name, version, attrs, threadId = null) {
    const msgFamily = 'write-schema'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    utils.DbcUtil.requireStringNotNullOrEmpty(name, 'name')
    utils.DbcUtil.requireStringNotNullOrEmpty(version, 'version')
    utils.DbcUtil.requireArrayNotContainNull(attrs, 'attrs')

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
    var msg = this._getBaseMessage(this.msgNames.WRITE_SCHEMA)
    msg = this._addThread(msg)
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
module.exports = WriteSchema
