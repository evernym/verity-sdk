const uuid = require('uuid')
const request = require('request-promise-native')

const utils = require('../utils')

module.exports = class Protocol {
  constructor () {

  }

  getNewId () {
    return uuid()
  }

  async getMessageBytes (context, message) {
    return utils.packMessageForVerity(context, message)
  }

  async sendMessage (context, message) {
    await request.post(context.verityUrl, await this.getMessageBytes())
  }
}
