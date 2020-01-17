'use strict'
const uuid = require('uuid')
const indy = require('./indy')
const request = require('request-promise-native')

exports.constants = {
  EVERNYM_MSG_QUALIFIER: 'did:sov:123456789abcdefghi1234',
  COMMUNITY_MSG_QUALIFIER: 'did:sov:BzCbsNYhMrjHiqZDTUASHg'
}

exports.miniId = function () {
  return uuid().split('-')[0]
}

exports.packMessageForVerity = async function (context, message) {
  const packedMessage = await exports.packMessage(context, context.verityPublicVerkey, message)
  return exports.prepareForwardMessage(context, packedMessage)
}

exports.packMessage = async function (context, receiverKey, message, anoncrypt = false) {
  indy.init()
  const encodedMessage = (new TextEncoder()).encode(JSON.stringify(message))
  if (anoncrypt) {
    return indy.sdk.packMessage(context.walletHandle, encodedMessage, [receiverKey], null)
  } else {
    return indy.sdk.packMessage(context.walletHandle, encodedMessage, [receiverKey], context.sdkPairwiseVerkey)
  }
}

exports.prepareForwardMessage = async function (context, packedMessage) {
  const forwardMessage = {
    '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
    '@fwd': context.verityPublicDID,
    '@msg': packedMessage
  }
  return exports.packMessage(context, context.verityPublicVerkey, forwardMessage, true)
}

exports.unpackMessage = async function (context, messageBytes) {
  indy.init()
  const message = JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, messageBytes))
  message.message = JSON.parse(message.message)
  return message
}

exports.sendPackedMessage = async function (uri, packedMessage) {
  return exports.httpPost(uri, new Uint8Array(packedMessage), 'application/octet-stream')
}

exports.httpGet = async function (uri) {
  const options = {
    uri: uri,
    json: true
  }
  return request.get(options)
}

exports.httpPost = async function (uri, message, contentType) {
  const options = {
    uri: uri,
    headers: {
      'Content-Type': contentType
    },
    body: message
  }
  return request.post(options)
}
