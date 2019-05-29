import * as vcx from 'node-vcx-wrapper'
import { IIssuerCredentialCreateData } from 'node-vcx-wrapper'
import uuid = require('uuid')
import { ICredential } from '..'
import { Agency, IAgencyConfig } from '../../../../agency'

export class UnfulfiledCredential {
    private credential: vcx.IssuerCredential
    private config: IAgencyConfig
    private message: ICredential
    private myConnection: vcx.Connection
    private myCredentialDef: vcx.CredentialDef
    private state: vcx.StateType

    constructor(
        message: ICredential, connection: vcx.Connection, credentialDef: vcx.CredentialDef, config: IAgencyConfig) {
        this.message = message
        this.myConnection = connection
        this.myCredentialDef = credentialDef
        this.config = config
    }

    public async offer() {
        const offerMsg: IIssuerCredentialCreateData = {
            attr: this.message.credentialData.credentialValues,
            credDefHandle: this.myCredentialDef.handle,
            sourceId: uuid(),
            credentialName: this.message.credentialData.credDefId,
            price: this.message.credentialData.price.toString(),
        }
        this.credential = await vcx.IssuerCredential.create(offerMsg)
        await this.credential.sendOffer(this.myConnection)
        Agency.postResponse(this.generateStatusReport(0, 'Offer sent to user'), this.config)
        this.state = await this.credential.getState()
        this.updateOfferState()
    }

    private async updateOfferState() {
        setTimeout(async () => {
            await this.credential.updateState()
            this.state = await this.credential.getState()
            if (this.state === vcx.StateType.RequestReceived) {
                Agency.postResponse(this.generateStatusReport(1, 'Offer accepted by user'), this.config)
                await this.credential.sendCredential(this.myConnection)
                Agency.postResponse(this.generateStatusReport(2, 'Credential sent to user'), this.config)
                this.state = await this.credential.getState()
                this.updateSentCredState()
            } else {
                this.updateOfferState()
            }}, 20000)
    }

    private async updateSentCredState() {
        setTimeout(async () => {
            await this.credential.updateState()
            this.state = await this.credential.getState()
            if (this.state === vcx.StateType.Accepted) {
                Agency.postResponse(this.generateStatusReport(3, 'Credential accepted by user'), this.config)
            } else {
                this.updateSentCredState()
            }}, 20000)
    }

    private generateStatusReport(status: number, statusMessage: string) {
        return {
            '@type': 'vs.service/credential/0.1/status',
            '@id': uuid(),
            'message': statusMessage,
            status,
            '~thread': {
                thid: this.message['@id'],
            },
        }
    }
}
