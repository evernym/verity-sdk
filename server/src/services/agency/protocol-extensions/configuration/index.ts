import { IAgentMessage, Protocol } from '..'
import { IAgencyConfig } from '../..'

export type ConfigurationProtocolTypes =
| 'vs.service/common/0.1/update_com_method'

export class Configuration extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage) {
        switch (message['@type']) {
            case 'vs.service/common/0.1/update_com_method':
                this.updateComMethod(message)
                return true
            default: return false
        }
    }

    private updateComMethod(message: IAgentMessage) {}
}
