import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IProofMessage } from '..'
import { Agency, IAgencyConfig } from '../../../'
import { generateProblemReport } from '../../../../../services/agency/utils/problem-reports'

export class UnfulfilledProof {

    private message: IProofMessage
    private myConnection: vcx.Connection
    private proof: vcx.Proof
    private config: IAgencyConfig
    private state: vcx.StateType

    constructor(message: IProofMessage, connection: vcx.Connection, config: IAgencyConfig) {
        this.message = message
        this.myConnection = connection
        this.config = config
    }

    public async proofRequest() {
        try {
            let revocationInterval = {}
            if (this.message.proofRequest.revocationInterval) {
                revocationInterval = this.message.proofRequest.revocationInterval
            }
            this.proof = await vcx.Proof.create({
                sourceId: uuid(),
                attrs: this.message.proofRequest.proofAttrs,
                name: this.message.proofRequest.name,
                revocationInterval,
            })
            await this.proof.requestProof(this.myConnection)
            Agency.postResponse(this.generateStatusReport(
                0, 'Successfully sent proof request', this.message), this.config)
            this.state = await this.proof.getState()
            this.updateProofState()
        } catch (e) {
            Agency.postResponse(generateProblemReport(
                'did:sov:123456789abcdefghi1234;spec/present-proof/0.1/problem-report',
                'failed to send proof request',
                this.message['@id']), this.config)
        }
    }

    private async updateProofState() {
        setTimeout(async () => {
            await this.proof.updateState()
            this.state = await this.proof.getState()
            if (this.state === vcx.StateType.Accepted) {
                const { proof, proofState } = await this.proof.getProof(this.myConnection)
                if (proof && proofState === vcx.ProofState.Verified) {
                    const proofJSON = JSON.parse(proof)
                    Agency.postResponse(this.generateStatusReport(
                        1, 'Proof recieved and validated', this.message, proofJSON.requested_proof), this.config)
                } else {
                    Agency.postResponse(generateProblemReport(
                        'did:sov:123456789abcdefghi1234;spec/present-proof/0.1/problem-report',
                        'proof recieved but was invalid!',
                        this.message['@id'],
                    ), this.config)
                }
            } else {
                this.updateProofState()
            }}, 2000)
    }

    private generateStatusReport(status: number, statusMessage: string, message: IProofMessage, content?: any) {
        let msg = {
            '@type': 'did:sov:123456789abcdefghi1234;spec/present-proof/0.1/status',
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
