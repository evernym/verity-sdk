import { IAgentMessage, Protocol } from '..'
import { IAgencyConfig } from '../..'
import { NewConnection } from './newConnection'

export type ConnectionProtocolTypes =
| 'did:sov:123456789abcdefghi1234;spec/connecting/0.6/CREATE_CONNECTION'
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connect/0.1/problem-report'
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/connect/0.1/status'

export class Connection extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage) {
        switch (message['@type']) {
            case 'did:sov:123456789abcdefghi1234;spec/connecting/0.6/CREATE_CONNECTION':
                const myConnection = new NewConnection(message, this.config)
                myConnection.connect()
                return true
            default: return false
        }
    }
}
