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
  return exports.packMessage(
    context.walletHandle,
    message,
    context.verityPairwiseDID,
    context.verityPairwiseVerkey,
    context.sdkPairwiseVerkey,
    context.verityPublicVerkey
    )
}

exports.packMessage = async function (walletHandle, message, pairwiseRemoteDID, pairwiseRemoteVerkey, pairwiseLocalVerkey, publicVerkey) {
  const msgBytes = Buffer.from(JSON.stringify(message), 'utf-8')
  const agentMsg = await indy.sdk.packMessage(walletHandle, msgBytes, [pairwiseRemoteVerkey], pairwiseLocalVerkey)
  const innerFwd = await exports.prepareForwardMessage(pairwiseRemoteDID, agentMsg)
  const innerFwdBytes = Buffer.from(JSON.stringify(innerFwd), 'utf-8')
  return indy.sdk.packMessage(walletHandle, innerFwdBytes, [publicVerkey], null)
}

exports.prepareForwardMessage = async function (toDid, packedMessage) {
  const forwardMessage = {
    '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
    '@fwd': toDid,
    '@msg': JSON.parse(packedMessage)
  }
  return forwardMessage
}

exports.unpackMessage = async function (context, messageBytes) {
  indy.init()
  const message = JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, messageBytes))
  message.message = JSON.parse(message.message)
  return message
}

exports.sendPackedMessage = async function (context, packedMessage) {
  const url = new URL(context.verityUrl)
  url.pathname = '/agency/msg'
  return exports.httpPost(url, packedMessage, 'application/octet-stream')
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
