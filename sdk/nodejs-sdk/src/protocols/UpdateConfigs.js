const Protocol = require('./Protocol')

module.exports = class UpdateConfigs extends Protocol {
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

  async updateMsg () {
    var msg = this._getBaseMessage(this.msgNames.UPDATE_CONFIGS)
    msg.configs = [
        {name: 'name', value: this.name},
        {name: 'logoUrl', value: this.logoUrl}
    ]

    return msg
  }

  async updateMsgPacked (context) {
    return this.getMessageBytes(context, await this.updateMsg())
  }

  async update (context) {
    await this.sendMessage(context, await this.updateMsgPacked(context))
  }

  async statusMsg (context) {
    var msg = this._getBaseMessage(this.msgNames.GET_STATUS)
    return msg
  }

  async statusMsgPacked (context) {
    return this.getMessageBytes(context, await this.statusMsg(context))
  }

  async status (context) {
    await this.sendMessage(context, await this.statusMsgPacked(context))
  }
}
