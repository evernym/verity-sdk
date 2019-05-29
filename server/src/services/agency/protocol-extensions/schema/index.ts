import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import { generateProblemReport } from '../../utils/problem-reports'

export type SchemaProtocolTypes =
| 'vs.service/schema/0.1/write'
| 'vs.service/schema/0.1/problem-report'
| 'vs.service/schema/0.1/status'

export interface ISchemaMessage extends IAgentMessage {
    schema: {
        name: string,
        version: string,
        attrNames: string[],
    }
}

export class Schema extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: ISchemaMessage) {
        switch (message['@type']) {
            case 'vs.service/schema/0.1/write':
                this.writeSchema(message)
                return true
            default: return false
        }
    }

    private async writeSchema(message: ISchemaMessage) {
        try {
            const schema = await vcx.Schema.create({
                sourceId: uuid(),
                paymentHandle: 0,
                data: message.schema,
            })
            Agency.postResponse(this.generateStatusReport(
                0, 'Successfully wrote schema to ledger', message, schema.schemaId), this.config)

        } catch (e) {
            Agency.postResponse(generateProblemReport(
                'vs.service/schema/0.1/problem-report',
                'failed to write schema to ledger',
                message['@id']), this.config)
        }
    }

    private generateStatusReport(status: number, statusMessage: string, message: ISchemaMessage, content?: any) {
        let msg = {
            '@type': 'vs.service/schema/0.1/status',
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
