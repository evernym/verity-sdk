import { IAgentMessage, Protocol } from '..'
import { IAgencyConfig } from '../..'
import { NewConnection } from './newConnection'

export type ConnectionProtocolTypes =
| 'vs.service/connection/0.1/new_connection'
| 'vs.service/connect/0.1/problem-report'
| 'vs.service/connect/0.1/status'

export interface IConnectionDetail {
    sourceId: string,
    phoneNo?: string
}
export class Connection extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage) {
        switch (message['@type']) {
            case 'vs.service/connection/0.1/new_connection':
                const myConnection = new NewConnection(message, this.config)
                myConnection.connect()
                return true
            default: return false
        }
    }
}
