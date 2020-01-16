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

exports.packMessageForVerity = async function (context, message, provisioning=false) {
  indy.init()
  let theirKey
  if(provisioning) {
    theirKey = context.verityPublicVerkey
  } else {
    theirKey = context.verityPairwiseVerkey
  }
  return indy.sdk.packMessage(context.walletHandle, new TextEncoder('utf-8').encode(JSON.stringify(message)), [theirKey], context.sdkPairwiseVerkey)
}

exports.unpackMessage = async function (context, messageBytes) {
  indy.init()
  const message = JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, messageBytes))
  message.message = JSON.parse(message.message)
  return message
}

exports.sendPackedMessage = async function (uri, packedMessage) {
  return exports.httpPost(uri, packedMessage, 'application/octet-stream')
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
