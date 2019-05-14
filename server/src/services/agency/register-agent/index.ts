// import * as indy from 'indy-sdk'
import * as vcx from 'node-vcx-wrapper';
var indy = require('indy-sdk')

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
   
    constructor() {
        this.Ready = new Promise(async (res, rej) => {
            try {
                const extn = new vcx.Extensions()
                const handle = extn.getWalletHandle()
                const [ did, verkey ] = await indy.createAndStoreMyDid(handle, {})
                this.config = {
                    myDID: did,
                    myVerkey: verkey,
                    fromDID: '',
                    fromVK: ''
                }
                console.log('agency config: ', this.config)
                res()
            } catch (e) {
                rej(e)
            }
        })
    }

    /**
     * GET for my did & verkey
     * new agent message CONNECT with:
     * { 
     * @type: 'vs.service/provision/1.0/connect' 
     * fromDID: string
     * fromDIDVerKey: string
     * }
     * 
     * this.config.fromVK = { fromVk }
     * this.config.fromDID = { fromDID }
     * 
     * Respond 
     * """
    connect_response = {
        "@type":'vs.service/provision/1.0/connect_response',
        "withPairwiseDID": this.config.myDID,
        "withPairwiseDIDVerKey": this.config.myVerkey
    }
    """
     *
     */
    connect() {}

     /**
     * SIGNUP
     *  {
        “@type”: 'vs.service/provision/1.0/signup'
        }
     *
     * RESPONSE
     * {
    “@type”: 'vs.service/provision/1.0/signup_response'
        }
        */
    signup() {}

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
    createAgent() {}
}