'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 UpdateConfigs protocol.
 */
module.exports = class UpdateConfigs extends Protocol {
  constructor (name = null, logoUrl = null) {
    /**
     * The name for the message family.
     */
    const msgFamily = 'update-configs'
    /**
     * The version for the message family.
     */
    const msgFamilyVersion = '0.6'
    /**
     * The qualifier for the message family. Uses Evernym's qualifier.
     */
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier)

    this.name = name
    this.logoUrl = logoUrl

    this.msgNames.UPDATE_CONFIGS = 'update'
    this.msgNames.GET_STATUS = 'get-status'
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #updateMsg
     */
  async updateMsg () {
    var msg = this._getBaseMessage(this.msgNames.UPDATE_CONFIGS)
    msg.configs = [
      { name: 'name', value: this.name },
      { name: 'logoUrl', value: this.logoUrl }
    ]

    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #updateMsg
     */
  async updateMsgPacked (context) {
    return this.getMessageBytes(context, await this.updateMsg())
  }

  /**
     * Directs verity-application to update the configuration register with the verity-application
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async update (context) {
    await this.sendMessage(context, await this.updateMsgPacked(context))
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #status
     */
  async statusMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    return msg
  }

  /**
     * Creates and packages message without sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the byte array ready for transport
     *
     * @see #status
     */
  async statusMsgPacked (context) {
    return this.getMessageBytes(context, await this.statusMsg(context))
  }

  /**
     * Ask for status from the verity-application agent
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }
}
