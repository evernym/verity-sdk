import indy from 'indy-sdk'
import * as vcx from 'node-vcx-wrapper'
import { Extensions } from 'node-vcx-wrapper';
import { protocolExtensionRouter } from '../../../protocol-extensions'

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
    public connect() {}

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
    public signup() {}

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
    public createAgent() {}

    private async unpackMsg(msg: Buffer) {
        const unpackedMsg = await this.extn.unpackMessage({ data: msg })
        console.log(unpackedMsg)
     }

    private packMsg(msg: string) { return msg }
}