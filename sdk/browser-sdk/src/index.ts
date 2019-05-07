import * as Axios from 'axios'
import { PackUnpack } from 'pack-unpack'
import { KeyManager } from './key-management'
import { Enroll, ICredField } from './protocols/enroll'

/**
 * Verity SDK has 3 major responsibilities:
 *
 */
export class VeritySDK {

    public readonly Ready: Promise<undefined>
    private enroll: Enroll = new Enroll()
    private keyManager: KeyManager = new KeyManager()
    private packUnpack: PackUnpack = new PackUnpack()
    private verityApiUrl!: string

    /**
     *
     * Sets up asyncronous internal classes
     * Performs a handshake with the verity application
     * Stores keys and makes them available in the verity SDK wallet
     * @param verityApiUrl: url of the verity backend ex: localhost:3000
     */
    constructor(verityApiUrl: string) {
        this.Ready = new Promise(async (res, rej) => {
            try {
                console.log('Initializing Verity-SDK')
                await this.keyManager.Ready
                await this.packUnpack.Ready
                this.verityApiUrl = verityApiUrl
                await this.setupKeys()
                res()
            } catch (e) {
                rej(e)
            }
        })
    }

    /**
     *
     * This method is used as the primary way of handling messages from the verity backend from the event source
     * created by the developer. After establishing an event stream (websockets, server sent events, etc) with the
     * backend, pass all messages passed from the backend to handleInboudMessage in order to decrpypt it.
     *
     * @param e message passed by the verity sdk
     * @param cb callback function that passes the decrypted data back to in order to be consumed by the applciation
     */
    public async handleInboundMessage(e: MessageEvent, cb: (data: any) => void) {
        try {
            const data = JSON.parse(e.data)
            if (!data.ciphertext) {
                cb(data)
                return
            }
            const keypair = await this.keyManager.getKeyPairDeserialized()
            const unpackedMsg = await this.packUnpack.unpackMessage(data, keypair)
            cb(unpackedMsg)
        } catch (err) {
            cb(err)
        }
    }

    public async newEnrollment(phoneNo: string, credendialDefId: string, credFields: ICredField[]) {
        const keypair = await this.keyManager.getKeyPairDeserialized()
        const agentPK = this.keyManager.getAgentKeyDeserialized()
        if (agentPK) {
            this.enroll.newEnrollment(phoneNo, credendialDefId, credFields, agentPK, keypair, this.verityApiUrl)
        }
    }

    private async setupKeys() {

        /**
         * Sets up the VeritySDK for use
         * Posts the newly generated public key and stores the key on return
         */
        try {
            console.log('Setting up keys for browser session')
            const pk = await this.keyManager.getPersonalPubKeySerialized()
            console.log('Performing handshake with Verity-Agent')
            const keys = await Axios.default.post(`${this.verityApiUrl}handshake`, pk)
            this.keyManager.setAgentKey(keys.data)
            console.log('Handshake Successful, storing public key of the Verity-Agent in Browser-Wallet')
        } catch (err) {
            console.log(
                'There was an error posting to the verity agent! Please check the verityApiUrl: ', this.verityApiUrl)
        }
    }
}
