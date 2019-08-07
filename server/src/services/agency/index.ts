import { Response } from 'express'
import fs = require('fs')
import * as vcx from 'node-vcx-wrapper'
import * as request from 'request-promise-native'
import { DataStore } from '../storage/appStorage'
import { Protocol } from './protocol-extensions'
import { generateProblemReport } from './utils/problem-reports'

export type AgencyMessageTypes =
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/connect'
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/connect_response'
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent'
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent_response'
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/signup'
| 'did:sov:123456789abcdefghi1234;spec/provision/1.0/signup_response'

export interface IAgencyConfig {
    myDID: string,
    myVerkey: string,
    fromDID: string
    fromVK: string,
    webhook: string,
}

export class Agency {

    public static inMemDB = new DataStore()

    public static async unpackMsg(msg: Buffer) {
        const extn = new vcx.Extensions()
        const unpackedMsg = await extn.unpackMessage({ data: msg })
        return unpackedMsg
    }

    public static async packMsg(msg: any, config: IAgencyConfig) {
        const extn = new vcx.Extensions()
        const receiverKeys = JSON.stringify([config.fromVK])
        const packedMsg = extn.packMessage({data: Buffer.from(JSON.stringify(msg)),
            keys: receiverKeys, sender: config.myVerkey})

        return packedMsg
    }

    public static async postResponse(msg: any, config: IAgencyConfig) {
        const body = await Agency.packMsg(msg, config)
        const myOptions: request.OptionsWithUri = {
            body,
            headers: {
                'content-type': 'application/octet-stream',
            },
            method: 'POST',
            uri: config.webhook,
        }
        await request.post(myOptions)
    }

    public readonly Ready: Promise<undefined>
    public config: IAgencyConfig
    private protocols: Protocol[]

    constructor(protocols: Protocol[]) {
        this.Ready = new Promise(async (res, rej) => {
            try {
                const vcxConfig = JSON.parse(fs.readFileSync('/etc/verity-server/vcxconfig.json').toString())
                this.config = {
                    fromDID: '',
                    fromVK: '',
                    myDID: vcxConfig.institution_did,
                    myVerkey: vcxConfig.institution_verkey,
                    webhook: '',
                }
                this.protocols = protocols
                this.protocols.forEach((protocol) => { protocol.updateConfig(this.config) })
                console.log('VerityConfig: ', this.config)
                res()
            } catch (e) {
                rej(e)
            }
        })
    }

    public setWebhook(webhook: string) {
        this.config.webhook = webhook
        this.protocols.forEach((protocol) => { protocol.updateConfig(this.config) })
    }

    public async newMessage(message: Buffer, res: Response) {
        try {
            const details = await this.unpackForwardMessage(message)
            console.log(details)
            if (details['@type'] === 'did:sov:123456789abcdefghi1234;spec/agent-provisioning/0.6/CREATE_AGENT') {
                const response = await this.connect(details)
                res.send(response)
            } else {
                if (!this.config.fromVK) {
                    console.error('You need to provision first! The in-memory data store has been reset.')
                } else {
                    for (let i = 0; i <= this.protocols.length; i++) {
                        const valid = this.protocols[i].router(details, this)
                        if (valid) { break }
                        if (i === this.protocols.length - 1) {
                            console.error(`Not a supported message type! ${details['@type']}`)

                            // This isn't going to work before the updateComMethod message.
                            Agency.postResponse(generateProblemReport(
                                'did:sov:123456789abcdefghi1234;spec/common/0.1/problem-report',
                                `Not a supported message type! ${details['@type']}`,
                                details['@id'],
                            ), this.config)
                        }
                    }
                }
                res.send()
            }
        } catch (e) {
            console.log(e)
        }
    }

    /**
     * GET for my did & verkey
     * new agent message CONNECT with:
     * {
     * @type: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/connect'
     * fromDID: string
     * fromDIDVerKey: string
     * }
     * this.config.fromVK = { fromVk }
     * this.config.fromDID = { fromDID }
     *
     * connect_response = {
     *   "@type":'did:sov:123456789abcdefghi1234;spec/provision/1.0/connect_response',
     *   "withPairwiseDID": this.config.myDID,
     *  "withPairwiseDIDVerKey": this.config.myVerkey
     * }
     *
     */
    public connect(message: any) {
        this.config.fromVK = message['fromDIDVerKey']
        this.config.fromDID = message['fromDID']
        const connectResponse = {
            ['@type']: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/connect_response',
            withPairwiseDID: this.config.myDID,
            withPairwiseDIDVerKey: this.config.myVerkey,
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }

    /**
     * SIGNUP
     *  {
     *  “@type”: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/signup'
     *  }
     *
     * RESPONSE
     * {
     *  “@type”: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/signup_response'
     * }
     */
    public signup() {
        const connectResponse = {
            ['@type']: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/signup_response',
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }

    /**
     * PROVISION
     * {
     *    “@type”: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent'
     * }
     *
     * Response: {
     *   “@type”: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent_response',
     *   "withPairwiseDID": this.config.myDID,
     *   "withPairwiseDIDVerKey": this.config.myVerkey
     * }
     */
    public createAgent() {
        const connectResponse = {
            ['@type']: 'did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent_response',
            withPairwiseDID: this.config.myDID,
            withPairwiseDIDVerKey: this.config.myVerkey,
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }

    private async unpackForwardMessage(message: Buffer) {
        const unpackedForwardMessageBuffer = await Agency.unpackMsg(message)
        const unpackedForwardMessageString = unpackedForwardMessageBuffer.toString('utf-8')
        const unpackedForwardMessage = JSON.parse(JSON.parse(unpackedForwardMessageString).message)
        const unpackedForwardMessageMsg = JSON.stringify(unpackedForwardMessage['@msg'])
        const unpackedMessageBuffer = await Agency.unpackMsg(Buffer.from(unpackedForwardMessageMsg))
        const unpackedMessage = JSON.parse(unpackedMessageBuffer.toString('utf-8'))
        return JSON.parse(unpackedMessage.message)
    }
}
