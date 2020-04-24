const ConnectingV0x6 = require('./ConnectingV0x6')
const ConnectingV1x0 = require('./ConnectingV1x0')
const uuid = require('uuid')

module.exports = class Connecting {
  v0x6 (threadId = null, sourceId = uuid(), phoneNumber = null, includePublicDID = false) {
    return new ConnectingV0x6(threadId, sourceId, phoneNumber, includePublicDID)
  }

  v1x0 (parentThreadId, label, base64InviteURL) {
    return new ConnectingV1x0(parentThreadId, label, base64InviteURL)
  }
}
