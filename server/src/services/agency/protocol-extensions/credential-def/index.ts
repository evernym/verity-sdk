import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { generateProblemReport } from '../../utils/problem-reports'

export type CredentialDefProtocolTypes =
| 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/write'
| 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/problem-report'
| 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/status'

export interface ICredWriteMessage extends IAgentMessage {
    'schemaId': string
}

export class CredentialDef extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: ICredWriteMessage) {
        switch (message['@type']) {
            case 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/write':
                this.writeCredDef(message)
                return true
            default: return false
        }
    }

    private async writeCredDef(message: ICredWriteMessage) {
        try {
            let revocationDetails = {}
            if (message.revocation_details) {
                revocationDetails = message.revocation_details
            }
            const credDef = await vcx.CredentialDef.create({
                name: message.name,
                paymentHandle: 0,
                revocationDetails,
                schemaId: message.schemaId,
                sourceId: uuid(),
            })
            await Agency.inMemDB.setCredentialDef(credDef)
            const id = await credDef.getCredDefId()

            Agency.postResponse(
                this.generateStatusReport(
                    0, 'Successfully wrote credential definition to ledger', message, id),
                    this.config,
                )
        } catch (e) {
            console.error('Failed to write credDef: ', e.message)
            Agency.postResponse(generateProblemReport(
                'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/problem-report',
                'Failed to write credential definition to ledger',
                message['@id']),
                this.config,
            )
        }
    }

    private generateStatusReport(status: number, statusMessage: string, message: ICredWriteMessage, content?: any) {
        let msg = {
            '@type': 'did:sov:123456789abcdefghi1234;spec/write-cred-def/0.1/status',
            '@id': uuid(),
            '~thread': {
                pthid: message['@id'],
                seqnum: 0,
            },
            status,
            'message': statusMessage,
        }
        if (content) { msg = Object.assign({}, { ...msg, content}) }
        return msg
    }
}
