'use strict'
const { v4: uuidv4 } = require('uuid')
const URL = require('url').URL
const indy = require('./indy')
const request = require('request-promise-native')
const WrongSetupError = require('./WrongSetupError')
const DbcUtil = require('./DbcUtil')

const useNewQualifierFormat = false

exports.constants = {
  EVERNYM_MSG_QUALIFIER: (useNewQualifierFormat ? 'https://didcomm.evernym.com' : 'did:sov:123456789abcdefghi1234;spec'),
  COMMUNITY_MSG_QUALIFIER: (useNewQualifierFormat ? 'https://didcomm.org' : 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec'),
  USE_NEW_QUALIFIER_FORMAT: useNewQualifierFormat
}

exports.WrongSetupError = WrongSetupError

exports.miniId = function () {
  return uuidv4().split('-')[0]
}

/**
 * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
 * public keys for encryption. The encryption and instructor is defined by the Aries community.
 *
 * @param context an instance of the Context object initialized to a verity-application agent
 * @param message the JSON message to be communicated to the verity-application
 * @return the byte array of the packaged and encrypted message
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
 */
exports.packMessageForVerity = async function (context, message) {
  return exports.packMessage(
    context.walletHandle,
    message,
    context.domainDID,
    context.verityAgentVerKey,
    context.sdkVerKey,
    context.verityPublicVerKey
  )
}

/**
 * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
 * public keys for encryption. The encryption and instructor is defined by the Aries community.
 *
 * @param walletHandle the handle to a created and open Indy wallet
 * @param message the JSON message to be communicated to the verity-application
 * @param pairwiseRemoteDID the domain DID of the intended recipient agent on the verity-application
 * @param pairwiseRemoteVerkey the verkey for the agent on the verity-application
 * @param pairwiseLocalVerkey the authorized verkey in the local wallet for the verity-sdk application
 * @param publicVerkey the public verkey for the verity-application
 * @return the byte array of the packaged and encrypted message
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
 */
exports.packMessage = async function (walletHandle, message, pairwiseRemoteDID, pairwiseRemoteVerkey, pairwiseLocalVerkey, publicVerkey) {
  indy.init()
  const msgBytes = Buffer.from(JSON.stringify(message), 'utf-8')
  const agentMsg = await indy.sdk.packMessage(walletHandle, msgBytes, [pairwiseRemoteVerkey], pairwiseLocalVerkey)
  const innerFwd = await exports.prepareForwardMessage(pairwiseRemoteDID, agentMsg)
  const innerFwdBytes = Buffer.from(JSON.stringify(innerFwd), 'utf-8')
  return indy.sdk.packMessage(walletHandle, innerFwdBytes, [publicVerkey], null)
}

/**
 * Prepares (pre-encryption) a forward message to a given DID that contains the given byte array message
 * @param toDID the DID the forward message is intended for
 * @param packedMessage the packaged and encrypted message that is being forwarded
 * @return the prepared JSON forward structure
 */
exports.prepareForwardMessage = async function (toDID, packedMessage) {
  const forwardMessage = {
    '@type': (useNewQualifierFormat ? 'https://didcomm.evernym.com/routing/1.0/FWD' : 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD'),
    '@fwd': toDID,
    '@msg': JSON.parse(packedMessage)
  }
  return forwardMessage
}

/**
 * Extracts the Forward message in the byte array that has been packaged and encrypted for a key that is locally held.
 *
 * @param context an instance of the Context object initialized to a verity-application agent
 * @param messageBytes the message received from the verity-application agent
 * @return an unencrypted messages as a JSON object
 */
exports.unpackForwardMessage = async function (context, messageBytes) {
  indy.init()
  const forwardMessage = await exports.unpackMessage(context, messageBytes)
  const message = JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, Buffer.from(JSON.stringify(forwardMessage['@msg']))))
  message.message = JSON.parse(message.message)
  return message
}

/**
 * Extracts the message in the byte array that has been packaged and encrypted for a key that is locally held.
 *
 * @param context an instance of the Context object initialized to a verity-application agent
 * @param messageBytes the message received from the verity-application agent
 * @return an unencrypted messages as a JSON object
 */
exports.unpackMessage = async function (context, messageBytes) {
  return JSON.parse(JSON.parse(await indy.sdk.unpackMessage(context.walletHandle, messageBytes)).message)
}

/**
 * Sends a packed message
 * @param context an instance of the Context object initialized to a verity-application agent
 * @param packedMessage the message received from the verity-application agent
 */
exports.sendPackedMessage = async function (context, packedMessage) {
  const url = new URL(context.verityUrl)
  url.pathname = '/agency/msg'
  return exports.httpPost(url, packedMessage, 'application/octet-stream')
}

/**
 * Gets a message
 * @param uri
 */
exports.httpGet = async function (uri) {
  const options = {
    uri: uri,
    json: true
  }
  return request.get(options)
}

/**
 * Posts a message
 * @param uri
 * @param message
 * @param contentType
 */
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

/**
 * Generate a random number
 */
exports.randInt = function (max) {
  return Math.floor(Math.random() * Math.floor(max))
}

exports.DbcUtil = DbcUtil
