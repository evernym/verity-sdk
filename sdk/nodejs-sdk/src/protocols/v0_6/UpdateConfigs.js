'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

/**
 * An interface for controlling a 0.6 UpdateConfigs protocol.
 * @extends Protocol
 */
class UpdateConfigs extends Protocol {
  /**
   * Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update
   * the endpoint.
   *
   * @param name Organization's name that is presented to third-parties
   * @param logoUrl A url to a logo that is presented to third-parties
   * @return 0.6 UpdateConfigs object
   *
   * @property {String} msgFamily - 'update-configs'
   * @property {String} msgFamilyVersion - '0.6'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.UPDATE_CONFIGS - 'update'
   * @property {String} this.msgNames.GET_STATUS - 'get-status'
   */
  constructor (name = null, logoUrl = null) {
    const msgFamily = 'update-configs'
    const msgFamilyVersion = '0.6'
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
module.exports = UpdateConfigs
