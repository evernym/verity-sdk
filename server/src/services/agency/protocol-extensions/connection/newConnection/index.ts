import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage } from '../..'
import { Agency, IAgencyConfig } from '../../../../agency'

export class NewConnection {
    private message: IAgentMessage
    private config: IAgencyConfig
    private myConnection: vcx.Connection
    private state: vcx.StateType

    constructor(message: IAgentMessage, config: IAgencyConfig) {
        this.message = message
        this.config = config
    }

    public async connect() {
        this.myConnection = await vcx.Connection.create({ id: this.message.connectionDetail.sourceId })
        let data: string
        if (this.message.connectionDetail.phoneNo) {
            data = `{"connection_type":"SMS","phone":"${this.message.connectionDetail.phoneNo}"}`
        } else {
            data = '{"connection_type":"QR"}'
        }
        await this.myConnection.connect({ data })
        const inviteDetails = await this.myConnection.inviteDetails(true)
        const report = this.generateStatusReport(0, inviteDetails)
        Agency.postResponse(report, this.config)
        this.state = await this.myConnection.getState()
        this.updateState()
    }

    private async updateState() {
        setTimeout(async () => {
            await this.myConnection.updateState()
            this.state = await this.myConnection.getState()
            if (this.state === vcx.StateType.Accepted) {
                Agency.inMemDB.setConnection(this.message.connectionDetail.sourceId, this.myConnection)
                const statusReport = this.generateStatusReport(0, this.message.connectionDetail.sourceId)
                const response = await Agency.packMsg(statusReport, this.config)
                Agency.postResponse(response, this.config)
            } else {
                this.updateState()
            }}, 20000)
    }

    private async generateStatusReport(status: number, statusMessage: string) {
        return {
            '@id': uuid(),
            '@type': 'vs.service/connection/0.1/status',
            'message': statusMessage,
            status,
            '~thread': {
                thid: this.message['@id'],
            },
        }
    }
}
