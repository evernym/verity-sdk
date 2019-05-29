import uuid = require('uuid')
import { IAgentMessage, ProtocolTypes } from '../../protocol-extensions'

interface IProblemReport extends IAgentMessage {
    'comment': {
        'en': string,
        'code': number,
    },
    'timestamp': Date

}

export function generateProblemReport(
    type: ProtocolTypes, comment: string, pthid?: string): IProblemReport {
    const problemReport: IProblemReport = {
        '@type': type,
        '@id': uuid(),
        'comment': {
            en: comment,
            code: 1, // Hard code all error codes to 1 in mock Verity
        },
        'timestamp': new Date(),
    }

    if (pthid) {
        problemReport['~thread'] = {
            pthid,
        }
    }
    return problemReport
}
