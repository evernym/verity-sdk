import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage, Protocol } from '..'
import { IAgencyConfig } from '../..';

export type ConnectionProtocolTypes =
| 'vs.service/connection/0.1/new_connection'
| 'vs.service/connect/0.1/problem-report'
| 'vs.service/connect/0.1/status'

export class Connection extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IAgentMessage) {
        switch (message['@type']) {
            case 'vs.service/connection/0.1/new_connection':
                this.newConnection(message)
                return true
            default: return false
        }
    }

    private async newConnection(message: IAgentMessage) {
        const { connectionDetail } = message
        const connection = await vcx.Connection.create({ id: connectionDetail.sourceId })
        const data = `{"connection_type":"SMS","phone":"${connectionDetail.phoneNo}"}`
        await connection.connect({ data })
        this.generateStatusReport(message, 0, 'test')
    }

    private async generateStatusReport(message: IAgentMessage, status: number, statusMessage: string) {
        return {
            '@id': uuid(),
            '@type': 'vs.service/connection/0.1/status',
            'message': statusMessage,
            status,
            '~thread': {
                thid: message['@id'],
            },
        }
    }
}
