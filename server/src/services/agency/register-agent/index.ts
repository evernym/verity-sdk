import { KeyPair } from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack';

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

    public config: IAgencyConfig
    private keypair: KeyPair
    private packUnpack: PackUnpack

    /**
     * Get wallet handle
     * const extn = new vcx.Extensions()
     * const handle = entn.getWalletHandle()
     * 
     * Call indy to create new DID
     * const { did, verkey } = Indy.did.createAndStoreMyDID(handle, {})
     */

    constructor() {
        this.config = {
            myDID: 'VSNODE_AGENCY_DID_12345',
            myVerkey: new TextDecoder('utf-8').decode(this.keypair.publicKey),
            fromDID: '',
            fromVK: ''
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
     *
     * SIGNUP
     *  {
        “@type”: 'vs.service/provision/1.0/signup'
        }
     *
     * RESPONSE
     * {
    “@type”: 'vs.service/provision/1.0/signup_response'
        }
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

    connect() {}
    signup() {}
    createAgent() {}
}