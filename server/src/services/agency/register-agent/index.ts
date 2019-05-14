import indy from 'indy-sdk'
import * as vcx from 'node-vcx-wrapper'
import { Extensions } from 'node-vcx-wrapper'
import { Response } from 'express';
// import { protocolExtensionRouter } from '../../../protocol-extensions'

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
}

export class Agency {

    public readonly Ready: Promise<undefined>
    public config: IAgencyConfig
    private extn: Extensions

    constructor() {
        this.Ready = new Promise(async (res, rej) => {
            try {
                this.extn = new vcx.Extensions()
                const handle = this.extn.getWalletHandle()
                const [ did, verkey ] = await indy.createAndStoreMyDid(handle, {})
                this.config = {
                    fromDID: '',
                    fromVK: '',
                    myDID: did,
                    myVerkey: verkey,
                }
                console.log('agency config: ', this.config)
                res()
            } catch (e) {
                rej(e)
            }
        })
    }

    public newMessage(message: Buffer) {
        this.unpackMsg(message)
    }

    public async provision(packedMessage: Buffer, res: Response) {
        const message = await this.unpackMsg(packedMessage)
        const messageString = message.toString('utf8')
        const outerMessage = JSON.parse(messageString)
        const jsonMessage = JSON.parse(outerMessage.message)
        console.log('RECEIVED PROVISION MESSAGE\n', jsonMessage, '\n******************************')
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
                    console.log('NOT A RECOGNIZED MESSAGE!: ', jsonMessage['@type'])
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
        console.log('processing message: ', message)
        this.config.fromVK = message['fromDIDVerKey']
        this.config.fromDID = message['fromDID']
        const connectResponse = {
            ['@type']: 'vs.service/provision/1.0/connect_response',
            withPairwiseDID: this.config.myDID,
            withPairwiseDIDVerKey: this.config.myVerkey,
        }
        const receiverKeys = JSON.stringify([this.config.fromVK])
        console.log(receiverKeys)

        const response = this.extn.packMessage({data: Buffer.from(JSON.stringify(connectResponse)),
            keys: receiverKeys, sender: this.config.myVerkey})

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
        const receiverKeys = JSON.stringify([this.config.fromVK])
        console.log(receiverKeys)

        const response = this.extn.packMessage({data: Buffer.from(JSON.stringify(connectResponse)),
            keys: receiverKeys, sender: this.config.myVerkey})

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
        const receiverKeys = JSON.stringify([this.config.fromVK])
        console.log(receiverKeys)

        const response = this.extn.packMessage({data: Buffer.from(JSON.stringify(connectResponse)),
            keys: receiverKeys, sender: this.config.myVerkey})

        return response
    }

    private async unpackMsg(msg: Buffer) {
        const unpackedMsg = await this.extn.unpackMessage({ data: msg })
        console.log(unpackedMsg)
        return unpackedMsg
     }

    // private packMsg(msg: string) { return msg }
}
