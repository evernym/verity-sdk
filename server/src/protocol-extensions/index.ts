import { Response } from 'express-serve-static-core'
import { INewEnrollmentMessage, newEnrollment } from './enroll';
import { KeyPair } from 'libsodium-wrappers';
// import uuid = require('uuid');

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

export type protocolMessage =
| INewEnrollmentMessage

/**
 * Routes the message to the appropriate protocol extension
 * @param message
 */
export const protocolExtensionRouter = (message: protocolMessage, responseHandle: Response, keypair: KeyPair, APK: Uint8Array) => {

    try {
        switch (message['@type']) {
            case 'vs.service/enroll/0.1/new-enrollment':
                newEnrollment(message, responseHandle, keypair, APK)
                return
            default:
                console.log('NOT A RECOGNIZED MESSAGE!: ', message)
                return
        }
    } catch (e) {
        return e
    }
}
