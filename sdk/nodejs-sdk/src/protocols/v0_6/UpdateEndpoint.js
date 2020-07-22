'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')

const COM_METHOD_TYPE = 2

/**
 * An interface for controlling a 0.6 UpdateEndpoint protocol.
 * @extends Protocol
 */
class UpdateEndpoint extends Protocol {
  /**
   * Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update
   * the endpoint.
   * @param threadId the thread id of the already started protocol
   * @return 0.6 UpdateEndpoint object
   *
   * @property {String} msgFamily - 'configs'
   * @property {String} msgFamilyVersion - '0.6'
   * @property {String} msgQualifier - 'Community Qualifier'
   * @property {String} this.msgNames.UPDATE_ENDPOINT - 'UPDATE_COM_METHOD'
   */
  constructor (threadId = null) {
    const msgFamily = 'configs'
    const msgFamilyVersion = '0.6'
    const msgQualifier = utils.constants.EVERNYM_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.msgNames.UPDATE_ENDPOINT = 'UPDATE_COM_METHOD'
  }

  /**
     * Creates the control message without packaging and sending it.
     * @param context an instance of the Context object initialized to a verity-application agent
     * @return the constructed message (JSON object)
     *
     * @see #updateMsg
     */
  async updateMsg (context) {
    if (context.endpointUrl === null || context.endpointUrl === '') {
      throw Error('Unable to update endpoint because context.endpointUrl is not defined')
    }
    const msg = this._getBaseMessage(this.msgNames.UPDATE_ENDPOINT)
    msg.comMethod = {
      id: 'webhook',
      type: COM_METHOD_TYPE,
      value: context.endpointUrl,
      packaging: {
        pkgType: '1.0',
        recipientKeys: [context.sdkVerKey]
      }
    }
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
    return this.getMessageBytes(context, await this.updateMsg(context))
  }

  /**
     * Directs verity-application to update the used endpoint for out-going signal message to the
     * endpoint contained in the context object. See: {@link Context#endpointUrl()}
     *
     * @param context an instance of the Context object initialized to a verity-application agent
     */
  async update (context) {
    await this.sendMessage(context, await this.updateMsgPacked(context))
  }
}
module.exports = UpdateEndpoint
