const IssueCredentialV06 = require('./IssueCredentialV06')
const IssueCredentialV1 = require('./IssueCredentialV1')

module.exports = class IssueCredential {
  v06 (forRelationship, threadId = null, name = null, values = null, credDefId = null) {
    return new IssueCredentialV06(forRelationship, threadId, name, values, credDefId)
  }

  v1 (forRelationship,
    credDefId,
    values = {},
    comment = '',
    price = 0) {
    return IssueCredentialV1(forRelationship, credDefId, values, comment, price)
  }

  v1ThreadId (forRelationship, threadId) {
    return new IssueCredentialV1(forRelationship, '', {}, '', 0, threadId)
  }
}
