'use strict'
const utils = require('../../utils')
const Protocol = require('../Protocol')
/*
 * NON_VISIBLE
 *
 * This is an implementation of OutOfBandImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static PresentProof class
 * @extends Protocol
 */
class OutOfBand extends Protocol {
  /**
     * Creates an OutOfBand Protocol
     * @param forRelationship the relationship identifier (DID) for the pairwise relationship that will be used
     * @param inviteUrl the Out-of-Band invitation url
     * @return OutOfBand object
     *
     * @property {String} msgFamily - 'out-of-band'
     * @property {String} msgFamilyVersion - '1.0'
     * @property {String} msgQualifier - 'Community Qualifier'
     * @property {String} this.msgNames.REUSE - 'reuse'
     */
  constructor (forRelationship = null, inviteUrl = null) {
    const msgFamily = 'out-of-band'
    const msgFamilyVersion = '1.0'
    const msgQualifier = utils.constants.COMMUNITY_MSG_QUALIFIER
    super(msgFamily, msgFamilyVersion, msgQualifier)

    this.msgNames.REUSE = 'reuse'
    this.msgNames.RELATIONSHIP_REUSED = 'relationship-reused'

    this.forRelationship = forRelationship
    this.inviteUrl = inviteUrl
  }

  /**
  * Creates the control message without packaging and sending it.
  *
  * @param context an instance of the Context object initialized to a verity-application agent
  * @return the constructed message (JSON object)
  *
  * @see #handshakeReuse
  */
  async reuseMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.REUSE)
    msg['~for_relationship'] = this.forRelationship
    msg.inviteUrl = this.inviteUrl
    msg = this._addThread(msg)
    return msg
  }

  /**
  * Creates and packages message without sending it.
  *
  * @param context an instance of the Context object initialized to a verity-application agent
  * @return the byte array ready for transport
  *
  * @see #handshakeReuse
  */
  async reuseMsgPacked (context) {
    return this.getMessageBytes(context, await this.reuseMsg(context))
  }

  /**
  * Direct the verity-application agent to reuse the relationship given.
  *
  * @param context an instance of the Context object initialized to a verity-application agent
  */
  async reuse (context) {
    await this.sendMessage(context, await this.reuseMsgPacked(context))
  }
}
module.exports = OutOfBand
