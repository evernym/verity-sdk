const PresentProofV06 = require('./PresentProofV06')
const PresentProofV1 = require('./PresentProofV1')

module.exports = class PresentProof {
  constructor (forRelationship, threadId = null, name = null, attributes = null, predicates = null) {
    return new PresentProofV06(forRelationship, threadId, name, attributes, predicates)
  }

  v1(parentThreadId, label, base64InviteURL) {
    return new PresentProofV1(parentThreadId, label, base64InviteURL)
  }
}