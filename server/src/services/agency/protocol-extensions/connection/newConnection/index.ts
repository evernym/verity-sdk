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
        this.myConnection = await vcx.Connection.create({ id: this.message.sourceId })
        const data = {
            connection_type: '',
            phone: null,
            use_public_did: false,
        }

        if (this.message.phoneNo) {
            data.connection_type = 'SMS'
            data.phone = this.message.phoneNo
        } else {
            data.connection_type = 'QR'
        }
        if (this.message.usePublicDid) {
            data.use_public_did = this.message.usePublicDid
        }

        await this.myConnection.connect({data: JSON.stringify(data)})
        const inviteDetails = await this.myConnection.inviteDetails(true)
        const report = this.generateStatusReport(0, 'Awaiting response', inviteDetails)
        Agency.postResponse(report, this.config)
        this.state = await this.myConnection.getState()
        this.updateState()
    }

    private async updateState() {
        setTimeout(async () => {
            await this.myConnection.updateState()
            this.state = await this.myConnection.getState()
            if (this.state === vcx.StateType.Accepted) {
                Agency.inMemDB.setConnection(this.message.sourceId, this.myConnection)
                const statusReport = this.generateStatusReport(
                    1,
                    'invite accepted!',
                    // FIXME: This should be the pairwise DID, not the given sourceId.
                    this.message.sourceId)
                Agency.postResponse(statusReport, this.config)
            } else {
                this.updateState()
            }}, 2000)
    }

    private generateStatusReport(status: number, statusMessage: string, content?: string) {
        return {
            '@id': uuid(),
            '@type': 'vs.service/connection/0.1/status',
            'message': statusMessage,
            status,
            '~thread': {
                thid: this.message['@id'],
            },
            'content': content,
        }
    }
}
