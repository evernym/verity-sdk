import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { generateProblemReport } from '../../utils/problem-reports'
import { UnfulfiledCredential } from './UnfulfilledCredential'

export type CredentialProtocolTypes =
| 'vs.service/credential/0.1/credential'
| 'vs.service/credential/0.1/problem-report'
| 'vs.service/credential/0.1/status'

export interface ICredential extends IAgentMessage {
    '@type': 'vs.service/credential/0.1/credential',
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

    private Credentials: UnfulfiledCredential[] = []

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
            Agency.postResponse(generateProblemReport(
                'vs.service/credential/0.1/problem-report',
                `No credential definition with id \"${message.credentialData.credDefId}\"`,
                message['@id']),
                this.config,
            )
            return
        }
        if (!connection) {
            Agency.postResponse(generateProblemReport(
                'vs.service/credential/0.1/problem-report',
                `No connection with id \"${message.connectionId}\"`,
                message['@id']),
                this.config,
            )
            return
        }

        const credObj = new UnfulfiledCredential(message, connection, credDef, this.config)
        await credObj.offer()
        this.Credentials.push(credObj)
    }
}
