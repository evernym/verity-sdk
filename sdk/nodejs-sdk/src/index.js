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
      Provision: require('./protocols/v0_6/Provision'),
      Connecting: require('./protocols/v0_6/Connecting'),
      IssueCredential: require('./protocols/v0_6/IssueCredential'),
      PresentProof: require('./protocols/v0_6/PresentProof'),
      IssuerSetup: require('./protocols/v0_6/IssuerSetup'),
      UpdateConfigs: require('./protocols/v0_6/UpdateConfigs'),
      UpdateEndpoint: require('./protocols/v0_6/UpdateEndpoint'),
      WriteCredentialDefinition: require('./protocols/v0_6/WriteCredentialDefinition'),
      WriteSchema: require('./protocols/v0_6/WriteSchema')
    },
    v0_7: {
      Provision: require('./protocols/v0_7/Provision')
    },
    v1_0: {
      Connecting: require('./protocols/v1_0/Connecting'),
      IssueCredential: require('./protocols/v1_0/IssueCredential'),
      PresentProof: require('./protocols/v1_0/PresentProof'),
      CommittedAnswer: require('./protocols/v1_0/CommittedAnswer'),
      Relationship: require('./protocols/v1_0/Relationship')
    }
  },
  utils: require('./utils/index'),
  Handlers: require('./utils/Handlers').Handlers,
  MessageFamily: require('./utils/MessageFamily')
}
