import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { generateProblemReport } from '../../utils/problem-reports'
import { UnfulfiledCredential } from './UnfulfilledCredential'

export type CredentialProtocolTypes =
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/issue-credential'
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/problem-report'
| 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/status'

export interface ICredential extends IAgentMessage {
    '@type': 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/issue-credential',
    'connectionId': string,
    'credentialData': {
        'id': string,
        'name': string,
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
            case 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/issue-credential':
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
                'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/problem-report',
                `No credential definition with id \"${message.credentialData.credDefId}\"`,
                message['@id']),
                this.config,
            )
            return
        }
        if (!connection) {
            Agency.postResponse(generateProblemReport(
                'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/issue-credential/0.1/problem-report',
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
