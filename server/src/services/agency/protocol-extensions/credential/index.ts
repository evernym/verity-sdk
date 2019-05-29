import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { UnfulfiledCredential } from './newCredential';

export type CredentialProtocolTypes =
| 'vs.service/credential/0.1/offer'
| 'vs.service/credential/0.1/credential'
| 'vs.service/credential/0.1/problem-report'
| 'vs.service/credential/0.1/status'

export interface ICredential extends IAgentMessage {
    '@type': 'vs.service/credential/0.1/credential',
    '~thread': {
        'pthid': string,
    },
    'connectionId': string,
    'credentialData': {
        'id': string,
        'credDefId': string,
        'credentialValues': {
            [key: string]: string,
        },
        'price': number,
    }
}

export class Credential extends Protocol {

    private Credentials: UnfulfiledCredential[]

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: ICredential) {
        switch (message['@type']) {
            case 'vs.service/credential/0.1/credential':
                this.newOffer(message)
                return true
            default:
                return false
        }
    }

    private async newOffer(message: ICredential) {
        const connection = Agency.inMemDB.getConnection(message.connectionId)
        const credDef = Agency.inMemDB.getCredentialDef(message.credentialData.credDefId)

        if (!credDef) {
            console.log('GEN PR for no creddef by id')
            return
        }
        if (!connection) {
            console.log('Gen PR for no connection by id')
            return
        }

        const credObj = new UnfulfiledCredential(message, connection, credDef, this.config)
        await credObj.offer()
        this.Credentials.push(credObj)
    }
}
