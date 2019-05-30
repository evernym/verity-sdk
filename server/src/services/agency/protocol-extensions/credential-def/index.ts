import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { generateProblemReport } from '../../utils/problem-reports'

export type CredentialDefProtocolTypes =
| 'vs.service/cred-def/0.1/write'
| 'vs.service/cred-def/0.1/problem-report'
| 'vs.service/cred-def/0.1/status'

export interface ICredWriteMessage extends IAgentMessage {
    'schemaId': string
}

export class CredentialDef extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: ICredWriteMessage) {
        switch (message['@type']) {
            case 'vs.service/cred-def/0.1/write':
                this.writeCredDef(message)
                return true
            default: return false
        }
    }

    private async writeCredDef(message: ICredWriteMessage) {
        try {
            const credDef = await vcx.CredentialDef.create({
                name: 'dummyCredDef',
                paymentHandle: 0,
                revocationDetails: {},
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
                'vs.service/cred-def/0.1/problem-report',
                'Failed to write credential definition to ledger',
                message['@id']),
                this.config,
            )
        }
    }

    private generateStatusReport(status: number, statusMessage: string, message: ICredWriteMessage, content?: any) {
        let msg = {
            '@type': 'vs.service/cred-def/0.1/status',
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
