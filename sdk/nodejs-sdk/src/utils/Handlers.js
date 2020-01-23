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
    if (!('@type' in message)) {
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
    const handlersKey = Handlers.buildHandlersKey(msgFamily, msgFamilyVersion)
    this.handlers[handlersKey] = new Handler(msgFamily, msgFamilyVersion, handlerFunction)
  }

  hasHandler (msgFamily, msgFamilyVersion) {
    return Handlers.buildHandlersKey(msgFamily, msgFamilyVersion) in this.handlers
  }

  setDefaultHandler (handlerFunction) {
    this.defaultHandler = handlerFunction
  }

  async handleMessage (context, rawMessage) {
    const message = await utils.unpackMessage(context, rawMessage)
    const msgType = MessageFamily.parseMessageType(message['@type'])
    const handlersKey = Handlers.buildHandlersKey(msgType.msgFamily, msgType.msgFamilyVersion)

    if (this.hasHandler(msgType.msgFamily, msgType.msgFamilyVersion)) {
      await this.handlers[handlersKey].handlerFunction(msgType.msgName, message)
    } else {
      if (this.defaultHandler) {
        await this.defaultHandler(msgType.msgName, message)
      }
    }
  }

  static buildHandlersKey (msgFamily, msgFamilyVersion) {
    return msgFamily + msgFamilyVersion
  }
}

module.exports = {
  Handler: Handler,
  Handlers: Handlers
}
