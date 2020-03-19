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
    this.defaultHandler = null // function that takes a raw message
  }

  addHandler (msgFamily, msgFamilyVersion, handlerFunction) {
    const handlersKey = Handlers.buildHandlersKey(msgFamily, msgFamilyVersion)
    this.handlers[handlersKey] = new Handler(msgFamily, msgFamilyVersion, handlerFunction)
  }

  hasHandler (msgFamily, msgFamilyVersion) {
    return Handlers.buildHandlersKey(msgFamily, msgFamilyVersion) in this.handlers
  }

  setDefaultHandler (defaultHandlerFunction) {
    this.defaultHandler = defaultHandlerFunction
  }

  async handleMessage (context, rawMessage) {
    const message = await utils.unpackMessage(context, rawMessage)
    const msgType = MessageFamily.parseMessageType(message['@type'])
    const handlersKey = Handlers.buildHandlersKey(msgType.msgFamily, msgType.msgFamilyVersion)

    if (this.hasHandler(msgType.msgFamily, msgType.msgFamilyVersion)) {
      await this.handlers[handlersKey].handlerFunction(msgType.msgName, message)
    } else {
      if (this.defaultHandler) {
        await this.defaultHandler(message)
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
