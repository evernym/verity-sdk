'use strict'
const utils = require('./index')
const MessageFamily = require('./MessageFamily')

/**
 * Defines how to handle a message of a certain type and optionally with a particular status
 */
class Handler {
  /**
   * Handler
   * @param {String} msgFamily - the family name for the message family
   * @param {String} msgFamilyVersion - the version for the message family
   * @param handlerFunction - function to be called to actually process the message
   */
  constructor (msgFamily, msgFamilyVersion, handlerFunction) {
    this.msgFamily = msgFamily
    this.msgFamilyVersion = msgFamilyVersion
    this.handlerFunction = handlerFunction
  }

  /**
   * Checks to see if this MessageHandler handles a particular agent message
   * @param message the JSON structure of the agent message
   * @return whether or not this Handler handles the given message
   */
  handles (message) {
    if (!('@type' in message)) {
      throw new Error('message does not contain a "@type" attribute')
    }
    const msgType = MessageFamily.parseMessageType(message['@type'])
    return msgType.msgFamily === this.msgFamily && msgType.msgFamilyVersion === this.msgFamilyVersion
  }
}

/**
 * Stores an array of message handlers that are used when receiving an inbound message
 */
class Handlers {
  /** Handlers */
  constructor () {
    this.handlers = {}
    this.defaultHandler = null // function that takes a raw message
  }

  /**
   * Adds a MessageHandler for a message type to the list if current message handlers
   * @param messageFamily the family of the message to be handled
   * @param messageFamilyVersion the message family version
   * @param handlerFunction the handler function itself
   */
  addHandler (msgFamily, msgFamilyVersion, handlerFunction) {
    const handlersKey = Handlers.buildHandlersKey(msgFamily, msgFamilyVersion)
    this.handlers[handlersKey] = new Handler(msgFamily, msgFamilyVersion, handlerFunction)
  }

  /** Checks to see if there is a handler set already
   * @param messageFamily the family of the message to be handled
   * @param messageFamilyVersion the message family version
   * @return bool
   */
  hasHandler (msgFamily, msgFamilyVersion) {
    return Handlers.buildHandlersKey(msgFamily, msgFamilyVersion) in this.handlers
  }

  /**
   * Adds a handler for all message types not handled by other message handlers
   * @param defaultHandlerFunction the function that will be called
   */
  setDefaultHandler (defaultHandlerFunction) {
    this.defaultHandler = defaultHandlerFunction
  }

  /**
   * Calls all of the handlers that support handling of this particular message type and message status
   * @param context an instance of the Context object initialized to a verity-application agent
   * @param rawMessage the raw bytes received from Verity
   */
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

  /**
   * @param msgFamily the family of the message to be handled
   * @param msgFamilyVersion the message family version
   * @return msgFamily + msgFamilyVersion
   */
  static buildHandlersKey (msgFamily, msgFamilyVersion) {
    return msgFamily + msgFamilyVersion
  }
}

module.exports = {
  Handler: Handler,
  Handlers: Handlers
}
