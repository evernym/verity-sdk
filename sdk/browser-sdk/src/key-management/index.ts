import { KeyType } from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack'

export interface IJSONKeyPair {
    keyType: KeyType,
    privateKey: number[],
    publicKey: number[],
}

export class KeyManager {

    /**
     * The Ready property will resolve when the KeyManager object instance is ready for use
     */
    public readonly Ready: Promise<undefined>
    private PackUnpack!: PackUnpack

    /**
     *
     * Creates a new KeyManager object. The returned object contains a .Ready property:
     * a promise that must be resolved before the KeyManager object can be used.
     */
    constructor() {
        this.Ready = new Promise(async (res, rej) => {
            try {
                this.PackUnpack = new PackUnpack()
                await this.PackUnpack.setup()
                res()
            } catch (e) {
                rej(e)
            }
        })
    }

    /**
     *
     * Returns a Sodium keypair, if no keypair exists a new one is generated and stored and returned
     */
    // public async getKeyPair(): Promise<KeyPair> {
    //     const keys = localStorage.getItem(KeyConstants.personalKeyPair)
    //     if (keys) {
    //         return this.parseKeysFromJSON(JSON.parse(keys))
    //     } else {
    //         const newKeys = await this.PackUnpack.generateKeyPair()
    //         this.storePersonalKeys(newKeys)
    //         return newKeys
    //     }
    // }

    // /**
    //  * Returns the keypair in JSON format for transport
    //  */
    // public getKeyPairJSON(): string | null {
    //     return localStorage.getItem(KeyConstants.personalKeyPair)
    // }

    // /**
    //  * Returns the public key for transport
    //  */
    // public getPublicKeyTransport(): number[] | null {
    //     const pk = localStorage.getItem(KeyConstants.personalKeyPair)
    //     if (pk) {
    //         const tmp = JSON.parse(pk)
    //         return tmp.publicKey
    //     } else {
    //         return null
    //     }
    // }

    // /**
    //  *
    //  * Returns a sodium agent PK
    //  */
    // public getAgentKey(): Uint8Array | null {
    //     const key = localStorage.getItem(KeyConstants.agentPK)
    //     if (key) {
    //         return Uint8Array.from(JSON.parse(key))
    //     } else {
    //         return null
    //     }
    // }

    // /**
    //  * Returns the JSON format of the agent PK for transport
    //  */
    // public getAgentKeyJSON(): string | null {
    //     return localStorage.getItem(KeyConstants.agentPK)
    // }

    // /**
    //  *
    //  * Stores an agents public key
    //  * @param APK string
    //  */
    // public storeAgentPK(APK: Uint8Array) {
    //     localStorage.setItem(KeyConstants.agentPK, JSON.stringify(Array.from(APK)))
    // }

    // /**
    //  * Rotates the key pair
    //  * @param key optional: if not provided a new keyPair is auto generated
    //  */
    // public async rotateKeyPair(key?: KeyPair) {
    //     if (key) {
    //         localStorage.setItem(KeyConstants.personalKeyPair, JSON.stringify(this.parseKeysForJSON(key)))
    //     } else {
    //         await this.generateKeyPair()
    //     }
    // }

    // /**
    //  * rotates the agent key
    //  * @param key string of the agent PK
    //  */
    // public rotateAgentKey(key: Uint8Array) {
    //     this.storeAgentPK(key)
    // }

    // private async generateKeyPair() {
    //     return await this.PackUnpack.generateKeyPair()
    // }

    // private parseKeysForJSON(keypair: KeyPair): IJSONKeyPair {
    //     return  {
    //         keyType: keypair.keyType,
    //         privateKey: Array.from(keypair.privateKey),
    //         publicKey: Array.from(keypair.publicKey),
    //     }
    // }

    // private parseKeysFromJSON(keypair: IJSONKeyPair): KeyPair {
    //     return {
    //         keyType: keypair.keyType,
    //         privateKey: Uint8Array.from(keypair.privateKey),
    //         publicKey: Uint8Array.from(keypair.publicKey),
    //     }
    // }

    // private storePersonalKeys(keys: KeyPair) {
    //     localStorage.setItem(KeyConstants.personalKeyPair, JSON.stringify(this.parseKeysForJSON(keys)))
    // }
}
