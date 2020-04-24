const IssueCredentialV0x6 = require('./IssueCredentialV0x6')
const IssueCredentialV1x0 = require('./IssueCredentialV1x0')

module.exports = class IssueCredential {
  v0x6 (forRelationship, threadId = null, name = null, values = null, credDefId = null) {
    return new IssueCredentialV0x6(forRelationship, threadId, name, values, credDefId)
  }

  v1x0 (forRelationship,
    credDefId,
    values = {},
    comment = '',
    price = 0) {
    return IssueCredentialV1x0(forRelationship, credDefId, values, comment, price)
  }

  v1x0ThreadId (forRelationship, threadId) {
    return new IssueCredentialV1x0(forRelationship, '', {}, '', 0, threadId)
  }
}
