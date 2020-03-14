'use strict'
const uuid = require('uuid')
const URL = require('url').URL
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
  indy.init()
  const msgBytes = Buffer.from(JSON.stringify(message), 'utf-8')
  const agentMsg = await indy.sdk.packMessage(walletHandle, msgBytes, [pairwiseRemoteVerkey], pairwiseLocalVerkey)
  const innerFwd = await exports.prepareForwardMessage(pairwiseRemoteDID, agentMsg)
  const innerFwdBytes = Buffer.from(JSON.stringify(innerFwd), 'utf-8')
  return indy.sdk.packMessage(walletHandle, innerFwdBytes, [publicVerkey], null)
}

exports.prepareForwardMessage = async function (toDID, packedMessage) {
  const forwardMessage = {
    '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
    '@fwd': toDID,
    '@msg': JSON.parse(packedMessage)
  }
  return forwardMessage
}

exports.unpackForwardMessage = async function (context, messageBytes) {
  indy.init()
  const forwardMessage = await exports.unpackMessage(context, messageBytes)
  const message = JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, Buffer.from(JSON.stringify(forwardMessage['@msg']))))
  message.message = JSON.parse(message.message)
  return message
}

exports.unpackMessage = async function (context, messageBytes) {
  return JSON.parse(JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, messageBytes)).message)
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

exports.randInt = function (max) {
  return Math.floor(Math.random() * Math.floor(max))
}

exports.truncateInviteDetailKeys = function (inviteDetails) {
  const truncatedInviteDetails = {
    sc: inviteDetails.statusCode,
    id: inviteDetails.connReqId,
    sm: inviteDetails.statusMsg,
    t: inviteDetails.targetName,
    version: inviteDetails.version,
    s: {
      n: inviteDetails.senderDetail.name,
      d: inviteDetails.senderDetail.DID,
      l: inviteDetails.senderDetail.logoUrl,
      v: inviteDetails.senderDetail.verKey,
      dp: {
        d: inviteDetails.senderDetail.agentKeyDlgProof.agentDID,
        k: inviteDetails.senderDetail.agentKeyDlgProof.agentDelegatedKey,
        s: inviteDetails.senderDetail.agentKeyDlgProof.signature
      }
    },
    sa: {
      d: inviteDetails.senderAgencyDetail.DID,
      e: inviteDetails.senderAgencyDetail.endpoint,
      v: inviteDetails.senderAgencyDetail.verKey
    }
  }

  if ('publicDID' in inviteDetails.senderDetail) {
    truncatedInviteDetails.s.publicDID = inviteDetails.senderDetail.publicDID
  }
  return JSON.stringify(truncatedInviteDetails)
}
