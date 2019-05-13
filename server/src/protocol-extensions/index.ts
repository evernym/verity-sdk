import { Response } from 'express-serve-static-core'
import { KeyPair } from 'libsodium-wrappers'
import { AgencyMessageTypes } from '../services/agency/register-agent'
import { IProtocols } from '../inbox'

export interface IAgentMessage {
    '@type': protocols
    '@id': string
    [key: string]: any
}

export type enrollProtocols =
| 'vs.service/enroll/0.1/new-enrollment'
| 'vs.service/enroll/0.1/problem-report'
| 'vs.service/enroll/0.1/status'

export type commonProtocols =
| 'vs.service/common/0.1/check-status'
| 'vs.service/common/0.1/invite-request'
| 'vs.service/common/0.1/invite-response'
| 'vs.service/common/0.1/problem-report'

export type protocols =
| commonProtocols
| enrollProtocols
| AgencyMessageTypes

/**
 * Routes the message to the appropriate protocol extension
 * @param message
 */
export const protocolExtensionRouter = (message: IAgentMessage, responseHandle: Response, keypair: KeyPair, APK: Uint8Array, protocols: IProtocols) => {
    try {
        switch (message['@type']) {
            case 'vs.service/provision/1.0/connect':
                protocols.agency.connect()
                return
            case 'vs.service/provision/1.0/create_agent':
                protocols.agency.createAgent()
                return
            case 'vs.service/provision/1.0/signup':
                protocols.agency.signup()
                return
            default:
                console.log('NOT A RECOGNIZED MESSAGE!: ', message)
                return
        }
    } catch (e) {
        return e
    }
}
