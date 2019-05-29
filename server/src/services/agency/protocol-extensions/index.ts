import { Agency, IAgencyConfig } from '..'
import { ConfigurationProtocolTypes } from './configuration'
import { ConnectionProtocolTypes } from './connection'
import { CredentialProtocolTypes } from './credential'
import { CredentialDefProtocolTypes } from './credential-def'
import { ProvableQuestionProtocolTypes } from './provable-question'
import { SchemaProtocolTypes } from './schema';

export interface IAgentMessage {
    '@type': ProtocolTypes
    '@id': string
    '~thread'?: {
        'pthid': string,
        'seqnum'?: number,
    }
    [key: string]: any
}

export type ProtocolTypes =
| ConfigurationProtocolTypes
| ConnectionProtocolTypes
| CredentialDefProtocolTypes
| CredentialProtocolTypes
| ProvableQuestionProtocolTypes
| SchemaProtocolTypes

export abstract class Protocol {

    protected config: IAgencyConfig

    constructor(config: IAgencyConfig) {
        this.config = config
    }

    public abstract router(message: IAgentMessage, agency?: Agency): boolean

    public updateConfig(config: IAgencyConfig) {
        this.config = config
    }
}
