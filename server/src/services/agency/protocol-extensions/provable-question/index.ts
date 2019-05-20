import * as vcx from 'node-vcx-wrapper'
import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'
import uuid = require('uuid');
import * as base64 from 'base-64'
import { Connection } from 'node-vcx-wrapper';

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
            {'text': string, 'nonce': string},
            {'text': string, 'nonce': string}
        ]
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

    private async newQuestion(message: IProvableQuestion) {
        const connection = Agency.inMemDB.getConnection(message.connection_id)
        if (!connection) {
            // FIXME: Send problem-report: NO CONNECTION WITH ID: ${message.connection_id} exists
            return
        } else {
            const question = {
                '@type': 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/question',
                '@id': uuid(),
                'question_text': message.question.question_text,
                'question_detail': message.question.question_detail,
                'valid_responses': message.question.valid_responses
            }
            await connection.sendMessage({
                msg: JSON.stringify(question),
                type: 'Question',
                title: 'Question'
            })
            const messageId = message['@id']
            const statusReport = this.generateStatusReport(0, messageId, "Question sent")
            Agency.postResponse(statusReport, this.config)

            this.updateState(connection, message)
        }
    }

    private async updateState(connection: Connection, message: IProvableQuestion) {
        setTimeout(async () => {
            const pairwiseDid = (await connection.serialize()).data.pw_did
            // @ts-ignore: vcx wrapper type needs to be updated. uids is an optional parameter.
            let messages = await vcx.downloadMessages({ status: 'MS-103', uids: null, pairwiseDids: pairwiseDid })
            messages = JSON.parse(messages)
            console.log(messages)
            let answer
            for (const message of messages[0].msgs) {
                if (message.type === 'Answer') {
                  if (answer) {
                    console.error('More then one "Answer" message')
                  } else {
                    answer = JSON.parse(JSON.parse(message['decryptedPayload'])['@msg'])
                  }
                  await vcx.updateMessages({ msgJson: JSON.stringify([{ 'pairwiseDID': pairwiseDid, 'uids': [message.uid] }]) })
                }
              }
            if (answer) {
                const signature = Buffer.from(answer['response.@sig']['signature'], 'base64')
                const data = answer['response.@sig']['sig_data']
                const valid = await connection.verifySignature({ data: Buffer.from(data), signature })
                if (valid) {
                    console.log('Signature is valid!')
                    const signedNonce = base64.decode(data)
                    for (let validResponse of message.question.valid_responses) {
                        if(validResponse.nonce === signedNonce) {
                            const statusReport = this.generateStatusReport(1, message['@id'], validResponse.text)
                            Agency.postResponse(statusReport, this.config)
                            return
                        }
                    }
                    // FIXME: Send problem report here. Matching valid response not found.
                } else {
                    console.log('Signature validation failed')
                    // FIXME: Send problem report. Signature validation failed.

                }
            } else {
                this.updateState(connection, message)
            }}, 20000)
    }

    private generateStatusReport(status: number, messageId: string, statusMessage: string) {
        return {
            '@id': uuid(),
            '@type': 'vs.service/question/0.1/status',
            'message': statusMessage,
            status,
            '~thread': {
                thid: messageId,
            },
        }
    }
}
