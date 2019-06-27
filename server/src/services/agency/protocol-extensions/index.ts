import { Agency, IAgencyConfig } from '..'
import { ConfigurationProtocolTypes } from './configuration'
import { ConnectionProtocolTypes } from './connection'
import { CredentialProtocolTypes } from './credential'
import { CredentialDefProtocolTypes } from './credential-def'
import { ProofProtocolTypes } from './proof'
import { ProvableQuestionProtocolTypes } from './provable-question'
import { SchemaProtocolTypes } from './schema'

export interface IAgentMessage {
    '@type': ProtocolTypes
    '@id': string
    '~thread'?: {
        'pthid': string,
        'seqnum'?: number,
    }
    [key: string]: any
}

export type BasicProtocolTypes =
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/common/0.1/problem-report'

export type ProtocolTypes =
| BasicProtocolTypes
| ConfigurationProtocolTypes
| ConnectionProtocolTypes
| CredentialDefProtocolTypes
| CredentialProtocolTypes
| ProvableQuestionProtocolTypes
| ProofProtocolTypes
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
