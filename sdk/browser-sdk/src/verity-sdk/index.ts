import Axios from 'axios'
import { PackUnpack } from 'pack-unpack'
import { KeyManager } from './key-management'
import { Enroll, ICredField } from './protocols/enroll'

export class VeritySDK {

    private enroll: Enroll = new Enroll()
    private keyManager: KeyManager = new KeyManager()
    private APK: Uint8Array
    private packUnpack: PackUnpack = new PackUnpack()
    private url: string

    /**
     * Creates a new VeirtySDK class. VeritySDK primarily handles key management, encryption of agent messages,
     * and handling of agent messages. This class expose all public methods needed to use full indy agent protocols
     * without the need of libindy.
     *
     * @param url url of the verity backend
     */
    constructor(url: string) {
        this.APK = new Uint8Array()
        this.url = url
    }

    /**
     *
     * Sets up keys (generates keypair for browser)
     * Sets up transport layer (sse is only one supported right now)
     *
     * Steps:
     * generate key pair
     * send public key to backend
     * await backend public key
     * set up sse connection with keys
     */
    public async setup() {

        await this.packUnpack.setup()
        await this.keyManager.generateKeyPairAndStore()

        /**
         * Posts the newly generated public key and stores the key on return
         */
        const keyPair = this.keyManager.getPublicKeyTransport()
        if (keyPair) {
            const keys = await Axios.post(`${this.url}handshake`, keyPair)
            this.APK = Uint8Array.from(keys.data)
        }
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
            console.log('Message from agent:', data)

            const keypair = this.keyManager.getKeyPair()
            if (keypair) {
                const unpackedMsg = await this.packUnpack.unpackMessage(e.data, keypair)
                cb(unpackedMsg)
            }
        } catch (err) {
            console.log('Message from agent: ', e.data)
        }
    }

    public async newEnrollment(phoneNo: string, credendialDefId: string, credFields: ICredField[]) {
        const keypair = this.keyManager.getKeyPair()
        if (keypair) {
            this.enroll.newEnrollment(phoneNo, credendialDefId, credFields, this.APK, keypair, this.url)
        }
    }
}

/**
 * Purpose of the verity sdk:
 * 1) to handle encryption for the end user
 * 2) to handle key management for the end user? Maybe this is not its purpose
 * 3) to handle agent messages and know how to respond to them?
 */
