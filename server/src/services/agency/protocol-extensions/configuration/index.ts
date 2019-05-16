import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'

export type ConfigurationProtocolTypes =
| 'vs.service/common/0.1/update_com_method'

export class Configuration extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage, agency: Agency) {
        switch (message['@type']) {
            case 'vs.service/common/0.1/update_com_method':
                this.updateComMethod(message, agency)
                console.log('updated config: ', this.config)
                return true
            default: return false
        }
    }

    private updateComMethod(message: IAgentMessage, agency: Agency) {
        switch (message.comMethod.type) {
            case 'webhook': agency.setWebhook(message.comMethod.value); break
            default: console.log('unsupported comMethod: ', message.comMethod.type)
        }
    }
}
