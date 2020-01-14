"use strict";
const uuid = require('uuid')
const sdk = require('indy-sdk')


exports.newId = function() {
    return uuid().split('-')[0]
}

exports.packMessageForVerity = async function(context, message) {
    return sdk.packMessage(context.walletHandle, new TextEncoder("utf-8").encode(JSON.stringify(message)), [context.verityPairwiseVerkey], context.sdkPairwiseVerkey)
}

exports.unpackMessageFromVerity = async function(context, messageBytes) {
    const message = JSON.parse(await sdk.unpackMessage(context.walletHandle, messageBytes))
    message.message = JSON.parse(message.message)
    return message
}