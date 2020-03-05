const Protocol = require('./Protocol')

module.exports = class UpdateConfigs extends Protocol {
  constructor (name, logoUrl, threadId = null) {
    const msgFamily = ''
    const msgFamilyVersion = ''
    const msgQualifier = ''
    super(msgFamily, msgFamilyVersion, msgQualifier, threadId)

    this.name = name
    this.logoUrl = logoUrl

    this.msgNames.UPDATE_CONFIGS = 'UPDATE_CONFIGS'
  }

  async updateMsg () {
    const msg = {
      '@type': { name: 'UPDATE_CONFIGS', ver: '1.0' },
      configs: [
        { name: 'name', value: this.name },
        { name: 'logoUrl', value: this.logoUrl }
      ]
    }
    return msg
  }

  async updateMsgPacked (context) {
    return this.getMessageBytes(context, await this.updateMsg())
  }

  async update (context) {
    await this.sendMessage(context, await this.updateMsgPacked(context))
  }
}
