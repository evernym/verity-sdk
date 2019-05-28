import * as base64 from 'base-64'
import { Connection } from 'node-vcx-wrapper'
import * as vcx from 'node-vcx-wrapper'
import uuid = require('uuid')
import { IAgentMessage, Protocol } from '..'
import { Agency, IAgencyConfig } from '../..'

export type ProvableQuestionProtocolTypes =
| 'vs.service/question/0.1/question'
| 'vs.service/question/0.1/problem-report'
| 'vs.service/question/0.1/status'

interface IProvableQuestion extends IAgentMessage {
    'connectionId': string,
    'question': {
        'question_text': string,
        'question_detail': string,
        'valid_responses': [
            {'text': string, 'nonce': string},
            {'text': string, 'nonce': string}
        ],
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
        const connection = Agency.inMemDB.getConnection(message.connectionId)
        if (!connection) {
            // FIXME: Send problem-report: NO CONNECTION WITH ID: ${message.connectionId} exists
            return
        } else {
            const question = {
                '@id': uuid(),
                '@type': 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/question',
                'question_text': message.question.question_text,
                'question_detail': message.question.question_detail,
                'valid_responses': message.question.valid_responses,
            }
            await connection.sendMessage({
                msg: JSON.stringify(question),
                type: 'Question',
                title: 'Question',
            })
            const messageId = message['@id']
            const statusReport = this.generateStatusReport(0, messageId, 'Question sent')
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
            for (const msg of messages[0].msgs) {
                if (msg.type === 'Answer') {
                  if (answer) {
                    console.error('More then one "Answer" message')
                  } else {
                    answer = JSON.parse(JSON.parse(msg['decryptedPayload'])['@msg'])
                  }
                  await vcx.updateMessages({ msgJson: JSON.stringify([{ pairwiseDID: pairwiseDid, uids: [msg.uid] }]) })
                }
              }
            if (answer) {
                const signature = Buffer.from(answer['response.@sig']['signature'], 'base64')
                const data = answer['response.@sig']['sig_data']
                const valid = await connection.verifySignature({ data: Buffer.from(data), signature })
                if (valid) {
                    console.log('Signature is valid!')
                    const signedNonce = base64.decode(data)
                    for (const validResponse of message.question.valid_responses) {
                        if (validResponse.nonce === signedNonce) {
                            const statusReport = this.generateStatusReport(1, message['@id'], "Question was answered", validResponse.text)
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

    private generateStatusReport(status: number, messageId: string, statusMessage: string, content?: string) {
        return {
            '@id': uuid(),
            '@type': 'vs.service/question/0.1/status',
            'message': statusMessage,
            status,
            '~thread': {
                thid: messageId,
            },
            'content': content
        }
    }
}
