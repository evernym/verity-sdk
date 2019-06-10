import { Response } from 'express'
import fs = require('fs')
import * as vcx from 'node-vcx-wrapper'
import * as request from 'request-promise-native'
import { DataStore } from '../storage/appStorage'
import { Protocol } from './protocol-extensions'
import { generateProblemReport } from './utils/problem-reports'

export type AgencyMessageTypes =
| 'vs.service/provision/1.0/connect'
| 'vs.service/provision/1.0/connect_response'
| 'vs.service/provision/1.0/create_agent'
| 'vs.service/provision/1.0/create_agent_response'
| 'vs.service/provision/1.0/signup'
| 'vs.service/provision/1.0/signup_response'

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

    public async newMessage(message: Buffer) {
        try {
            const unpackedMsg1 = await Agency.unpackMsg(message)
            const messageString = unpackedMsg1.toString('utf8')
            let outerMessage = JSON.parse(messageString)
            if (outerMessage['@type'] === 'fwd') {
                outerMessage = outerMessage['@msg']
            } else { outerMessage = outerMessage.message }
            const fullyUnpacked = await Agency.unpackMsg(Buffer.from(outerMessage))
            const unpacked = JSON.parse(fullyUnpacked.toString('utf8'))
            const details = JSON.parse(unpacked.message)
            if (!this.config.fromVK) {
                console.error('You need to provision first! The in-memory data store has been reset.')
            } else {
                for (let i = 0; i <= this.protocols.length; i++) {
                    const valid = this.protocols[i].router(details, this)
                    if (valid) { break }
                    if (i === this.protocols.length - 1) {
                        Agency.postResponse(generateProblemReport(
                            'vs.service/common/0.1/problem-report',
                            `Not a supported message type! ${details['@type']}`,
                            details['@id'],
                        ), this.config)
                    }
                }
            }
        } catch (e) {
            console.log(e)
        }
    }

    public async provision(packedMessage: Buffer, res: Response) {
        const message = await Agency.unpackMsg(packedMessage)
        const messageString = message.toString('utf8')
        const outerMessage = JSON.parse(messageString)
        const jsonMessage = JSON.parse(outerMessage.message)
        try {
            switch (jsonMessage['@type']) {
                case 'vs.service/provision/1.0/connect':
                    const response = await this.connect(jsonMessage)
                    res.send(response)
                    return
                case 'vs.service/provision/1.0/create_agent':
                    const createAgentResponse = await this.createAgent()
                    res.send(createAgentResponse)
                    return
                case 'vs.service/provision/1.0/signup':
                    const signupResponse = await this.signup()
                    res.send(signupResponse)
                    return
                default:
                    Agency.postResponse(generateProblemReport(
                        'vs.service/proof/0.1/problem-report',
                        `Not a recognized configuration message type: ${jsonMessage['@type']}`,
                        jsonMessage['@id'],
                    ), this.config)
                    return
            }
        } catch (e) {
            return e
        }
    }

    /**
     * GET for my did & verkey
     * new agent message CONNECT with:
     * {
     * @type: 'vs.service/provision/1.0/connect'
     * fromDID: string
     * fromDIDVerKey: string
     * }
     * this.config.fromVK = { fromVk }
     * this.config.fromDID = { fromDID }
     *
     * connect_response = {
     *   "@type":'vs.service/provision/1.0/connect_response',
     *   "withPairwiseDID": this.config.myDID,
     *  "withPairwiseDIDVerKey": this.config.myVerkey
     * }
     *
     */
    public connect(message: any) {
        this.config.fromVK = message['fromDIDVerKey']
        this.config.fromDID = message['fromDID']
        const connectResponse = {
            ['@type']: 'vs.service/provision/1.0/connect_response',
            withPairwiseDID: this.config.myDID,
            withPairwiseDIDVerKey: this.config.myVerkey,
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }

    /**
     * SIGNUP
     *  {
     *  “@type”: 'vs.service/provision/1.0/signup'
     *  }
     *
     * RESPONSE
     * {
     *  “@type”: 'vs.service/provision/1.0/signup_response'
     * }
     */
    public signup() {
        const connectResponse = {
            ['@type']: 'vs.service/provision/1.0/signup_response',
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }

    /**
     * PROVISION
     * {
     *    “@type”: 'vs.service/provision/1.0/create_agent'
     * }
     *
     * Response: {
     *   “@type”: 'vs.service/provision/1.0/create_agent_response',
     *   "withPairwiseDID": this.config.myDID,
     *   "withPairwiseDIDVerKey": this.config.myVerkey
     * }
     */
    public createAgent() {
        const connectResponse = {
            ['@type']: 'vs.service/provision/1.0/create_agent_response',
            withPairwiseDID: this.config.myDID,
            withPairwiseDIDVerKey: this.config.myVerkey,
        }
        const response = Agency.packMsg(connectResponse, this.config)
        return response
    }
}
