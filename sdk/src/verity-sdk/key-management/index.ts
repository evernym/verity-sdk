import * as _sodium from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack'

export interface IJSONKeyPair {
    keyType: _sodium.KeyType,
    privateKey: number[],
    publicKey: number[],
}

const KeyConstants = {
    agentPK: 'AGENT_PUBLIC_KEY',
    personalKeyPair: 'PERSONAL_KEY_PAIR',
}

/**
 *
 * Generates keys using pack/unpack gen key
 * Supports storing keys
 * Supports retrieving keys in both JSON (transport format) and Sodium keypair format for pack/unpack
 * Supports clearing keys
 * Supports rotating keys
 * class currently only supports storage of ONE KEY AT A TIME
 */
export class KeyManager {

    /**
     * Generates a libsodium keypair
     */
    public async generateKeyPairAndStore() {
        const paup = new PackUnpack()
        const keys = await paup.generateKeyPair()
        localStorage.setItem(KeyConstants.personalKeyPair, JSON.stringify(this.parseKeysForJSON(keys)))
    }

    /**
     *
     * Returns a Sodium keypair
     */
    public getKeyPair(): _sodium.KeyPair | null {
        const keys = localStorage.getItem(KeyConstants.personalKeyPair)
        if (keys) {
            return this.parseKeysFromJSON(JSON.parse(keys))
        } else {
            return null
        }
    }

    /**
     * Returns the keypair in JSON format for transport
     */
    public getKeyPairJSON(): string | null {
        return localStorage.getItem(KeyConstants.personalKeyPair)
    }

    /**
     * Returns the public key for transport
     */
    public getPublicKeyTransport(): number[] | null {
        const pk = localStorage.getItem(KeyConstants.personalKeyPair)
        if (pk) {
            const tmp = JSON.parse(pk)
            return tmp.publicKey
        } else {
            return null
        }
    }

    /**
     *
     * Returns a sodium agent PK
     */
    public getAgentKey(): Uint8Array | null {
        const key = localStorage.getItem(KeyConstants.agentPK)
        if (key) {
            return Uint8Array.from(JSON.parse(key))
        } else {
            return null
        }
    }

    /**
     * Returns the JSON format of the agent PK for transport
     */
    public getAgentKeyJSON(): string | null {
        return localStorage.getItem(KeyConstants.agentPK)
    }

    /**
     *
     * Stores an agents public key
     * @param APK string
     */
    public storeAgentPK(APK: Uint8Array) {
        localStorage.setItem(KeyConstants.agentPK, JSON.stringify(Array.from(APK)))
    }

    /**
     * Rotates the key pair
     * @param key optional: if not provided a new keyPair is auto generated
     */
    public async rotateKeyPair(key?: _sodium.KeyPair) {
        if (key) {
            localStorage.setItem(KeyConstants.personalKeyPair, JSON.stringify(this.parseKeysForJSON(key)))
        } else {
            await this.generateKeyPairAndStore()
        }
    }

    /**
     * rotates the agent key
     * @param key string of the agent PK
     */
    public rotateAgentKey(key: Uint8Array) {
        this.storeAgentPK(key)
    }

    private parseKeysForJSON(keypair: _sodium.KeyPair): IJSONKeyPair {
        return  {
            keyType: keypair.keyType,
            privateKey: Array.from(keypair.privateKey),
            publicKey: Array.from(keypair.publicKey),
        }
    }

    private parseKeysFromJSON(keypair: IJSONKeyPair): _sodium.KeyPair {
        return {
            keyType: keypair.keyType,
            privateKey: Uint8Array.from(keypair.privateKey),
            publicKey: Uint8Array.from(keypair.publicKey),
        }
    }
}
