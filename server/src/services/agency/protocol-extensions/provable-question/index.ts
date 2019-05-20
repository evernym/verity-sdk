import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'

export type ProvableQuestionProtocolTypes =
| 'vs.service/question/0.1/question'
| 'vs.service/question/0.1/problem-report'
| 'vs.service/question/0.1/status'

interface IProvableQuestion extends IAgentMessage {
    'connection_id': 'abcd12345'
    'question': {
        'question_text': string,
        'question_detail': string,
        'valid_responses': [
            {'text': string},
            {'text': string}
        ],
        '@timing': {
            'expires_time': Date,
        },
    }
}

export class ProvableQuestion extends Protocol {

    constructor(config: IAgencyConfig) {
        super(config)
    }

    public router(message: IProvableQuestion) {
        switch (message['@type']) {
            case 'vs.service/question/0.1/question':
                console.log('new provable question')
                this.newQuestion(message)
                return true
            default: return false
        }
    }

    private newQuestion(message: IProvableQuestion) {
        const connection = Agency.inMemDB.getConnection(message.connection_id)
        if (!connection) {
            console.log(`SEND STATUS REPORT: NO CONNECTION WITH ID: ${message.connection_id} exists`)
            return
        }
    }
}
