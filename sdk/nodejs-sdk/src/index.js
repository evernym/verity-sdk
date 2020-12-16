'use strict'

module.exports = {
  Context: require('./utils/Context'),
  ContextBuilder: require('./utils/ContextBuilder'),
  protocols: {
    Protocol: require('./protocols/Protocol'),
    UpdateEndpoint: require('./protocols/UpdateEndpoint'),
    UpdateConfigs: require('./protocols/UpdateConfigs'),
    IssuerSetup: require('./protocols/IssuerSetup'),
    WriteSchema: require('./protocols/WriteSchema'),
    WriteCredentialDefinition: require('./protocols/WriteCredentialDefinition'),
    CommittedAnswer: require('./protocols/CommittedAnswer'),
    BasicMessage: require('./protocols/BasicMessage'),
    v0_6: {
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
      BasicMessage: require('./protocols/v1_0/BasicMessage'),
      Relationship: require('./protocols/v1_0/Relationship'),
      GoalCodes: require('./protocols/v1_0/GoalCodes')
    }
  },
  utils: require('./utils/index'),
  Handlers: require('./utils/Handlers').Handlers,
  MessageFamily: require('./utils/MessageFamily')
}
