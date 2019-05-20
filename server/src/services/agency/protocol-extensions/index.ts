import { Agency, IAgencyConfig } from '..'
import { ConfigurationProtocolTypes } from './configuration'
import { ConnectionProtocolTypes } from './connection'
import { ProvableQuestionProtocolTypes } from './provable-question'

export interface IAgentMessage {
    '@type': ProtocolTypes
    '@id': string
    [key: string]: any
}

export type ProtocolTypes =
| ConnectionProtocolTypes
| ConfigurationProtocolTypes
| ProvableQuestionProtocolTypes

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
