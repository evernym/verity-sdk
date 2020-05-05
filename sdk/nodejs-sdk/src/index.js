'use strict'

module.exports = {
  Context: require('./utils/Context'),
  ContextBuilder: require('./utils/ContextBuilder'),
  protocols: {
    Protocol: require('./protocols/Protocol'),
    Provision: require('./protocols/Provision'),
    UpdateEndpoint: require('./protocols/UpdateEndpoint'),
    UpdateConfigs: require('./protocols/UpdateConfigs'),
    IssuerSetup: require('./protocols/IssuerSetup'),
    WriteSchema: require('./protocols/WriteSchema'),
    WriteCredentialDefinition: require('./protocols/WriteCredentialDefinition'),
    Connecting: require('./protocols/Connecting'),
    IssueCredential: require('./protocols/IssueCredential'),
    PresentProof: require('./protocols/PresentProof'),
    CommittedAnswer: require('./protocols/CommittedAnswer'),
    v0_6: {
      Connecting: require('./protocols/v0_6/Connecting'),
      IssueCredential: require('./protocols/v0_6/IssueCredential'),
      PresentProof: require('./protocols/v0_6/PresentProof')
    },
    v1_0: {
      Connecting: require('./protocols/v1_0/Connecting'),
      IssueCredential: require('./protocols/v1_0/IssueCredential'),
      PresentProof: require('./protocols/v1_0/PresentProof')
    }
  },
  utils: require('./utils/index'),
  Handlers: require('./utils/Handlers').Handlers,
  MessageFamily: require('./utils/MessageFamily')
}
