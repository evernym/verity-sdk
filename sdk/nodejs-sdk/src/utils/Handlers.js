'use strict'
const utils = require('./index')
const MessageFamily = require('./MessageFamily')

class Handler {
  constructor (msgFamily, msgFamilyVersion, handlerFunction) {
    this.msgFamily = msgFamily
    this.msgFamilyVersion = msgFamilyVersion
    this.handlerFunction = handlerFunction
  }

  handles (message) {
    if (!message.prototype.hasOwnProperty.call('@type')) {
      throw new Error('message does not contain a "@type" attribute')
    }
    const msgType = MessageFamily.parseMessageType(message['@type'])
    return msgType.msgFamily === this.msgFamily && msgType.msgFamilyVersion === this.msgFamilyVersion
  }
}

class Handlers {
  constructor () {
    this.handlers = {}
    this.defaultHandler = null
  }

  addHandler (msgFamily, msgFamilyVersion, handlerFunction) {
    const handlersKey = msgFamily + msgFamilyVersion
    this.handlers[handlersKey] = Handler(msgFamily, msgFamilyVersion, handlerFunction)
  }

  addDefaultHandler (handlerFunction) {
    this.defaultHandler = handlerFunction
  }

  async handleMessage (context, rawMessage) {
    const message = await utils.unpackMessage(rawMessage)
    const msgType = MessageFamily.parseMessageType(message['@type'])
    const handlersKey = msgType.msgFamily + msgType.msgFamilyVersion

    if (this.handlers.prototype.hasOwnProperty.call(handlersKey)) {
      await this.handlers[handlersKey].handlerFunction(msgType.msgName, message)
    } else {
      if (this.defaultHandler) {
        await this.defaultHandler(msgType.msgName, message)
      }
    }
  }
}

module.exports = {
  Handler: Handler,
  Handlers: Handlers
}
