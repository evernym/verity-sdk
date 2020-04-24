const PresentProofV0x6 = require('./PresentProofV0x6')
const PresentProofV1x0 = require('./PresentProofV1x0')

module.exports = class PresentProof {
  v0x6 (forRelationship, threadId = null, name = null, attributes = null, predicates = null) {
    return new PresentProofV0x6(forRelationship, threadId, name, attributes, predicates)
  }

  v1x0ThreadId (forRelationship, threadId) {
    return new PresentProofV1x0(forRelationship, threadId)
  }

  v1x0 (forRelationship, name = '', attributes = [], predicates = []) {
    return new PresentProofV1x0(forRelationship, null, name, attributes, predicates)
  }
}
