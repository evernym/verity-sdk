import * as vcx from 'node-vcx-wrapper'
import { Protocol, IAgentMessage } from '..'
import { IAgencyConfig, Agency } from '../..'
import uuid = require('uuid')

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
        Agency.postResponse(this.generateStatusReport(0, 'Write request received', message), this.config)

        const credDef = await vcx.CredentialDef.create({
            name: 'dummyCredDef',
            paymentHandle: 0,
            revocationDetails: {},
            schemaId: message.schemaId,
            sourceId: uuid(),
        })
        Agency.inMemDB.setCredentialDef(credDef)
        const id = await credDef.getCredDefId()

        Agency.postResponse(this.generateStatusReport(1, 'Successfully wrote credential definition to ledger', message, id), this.config)
    }

    private generateStatusReport(status: number, statusMessage: string, message: ICredWriteMessage, content?: any) {
        let msg = {
            '@type': 'vs.service/cred-def/0.1/status',
            '@id': uuid(),
            '~thread': {
                'pthid': message['@id'],
                'seqnum': 0
            },
            status,
            message: statusMessage
        }
        if (content) { msg = Object.assign({}, { ...msg, content}) }
        return msg
    }
}