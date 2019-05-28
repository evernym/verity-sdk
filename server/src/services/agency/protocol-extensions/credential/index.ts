import { IAgentMessage, Protocol } from '..';
import { IAgencyConfig } from '../..';

export type CredentialProtocolTypes = 
| 'vs.service/credential/0.1/offer'
| 'vs.service/credential/0.1/credential'
| 'vs.service/credential/0.1/problem-report'
| 'vs.service/credential/0.1/status'

interface ICredentialOffer extends IAgentMessage {
    'connectionId': string,
    'credDefId': string
}

interface ICredential extends IAgentMessage {
    "~thread": {
        "pthid": string
    },
    "connectionId": string,
    "credentialData":{
        "id": string,
        "credDefId": string,
        "credentialValues": {
            [key:string]: string
        },
        "price": number
    }
}

export class Credential extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage) { // FIXME: What type should message be? Needs to handle both types above.
        switch (message['@type']) {
            case 'vs.service/credential/0.1/offer':
                // Handle offer
                return true
            case 'vs.service/credential/0.1/credential':
                // Handle credential message
                return true
            default:
                return false
        }
    }


}