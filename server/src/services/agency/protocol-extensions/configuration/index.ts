import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'

export type ConfigurationProtocolTypes =
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/configs/0.6/UPDATE_COM_METHOD'

export class Configuration extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage, agency: Agency) {
        switch (message['@type']) {
            case 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/configs/0.6/UPDATE_COM_METHOD':
                this.updateComMethod(message, agency)
                console.log('updated config: ', this.config)
                return true
            default: return false
        }
    }

    private updateComMethod(message: IAgentMessage, agency: Agency) {
        switch (message.comMethod.type) {
            case 2: agency.setWebhook(message.comMethod.value); break
            default: console.log('unsupported comMethod: ', message.comMethod.type)
        }
    }
}
