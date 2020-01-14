"use strict";
const uuid = require('uuid')

exports.newId = function() {
    return uuid().split('-')[0]
}

exports.packMessageForVerity = async function(context, message) {

}

exports.unpackMessageFromVerity = async function(context, messageBytes) {

}