const ConnectingV06 = require('./ConnectingV06')
const ConnectingV1 = require('./ConnectingV1')

module.exports = class Connecting {
  constructor (threadId = null, sourceId = uuid(), phoneNumber = null, includePublicDID = false) {
    return new ConnectingV06(threadId, sourceId, phoneNumber, includePublicDID)
  }

  v1(parentThreadId, label, base64InviteURL) {
    return new ConnectingV1(parentThreadId, label, base64InviteURL)
  }
}