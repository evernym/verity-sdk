import { KeyPair } from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack'

export interface IJSONKeyPair {
    keyType: KeyType,
    privateKey: number[],
    publicKey: number[],
}

export class KeyManager {

    private init: boolean
    private KEYS: KeyPair
    private pu: PackUnpack

    public async setup() {
        if (!this.init) {
            this.pu = new PackUnpack()
            await this.pu.Ready
            const keys = await this.pu.generateKeyPair()
            this.KEYS = keys
        }
    }

    public returnPublicKeyTransport(): number[] | undefined {
        return Array.from(this.KEYS.publicKey)
    }

    public returnKeys(): KeyPair {
        return this.KEYS
    }

    public  async rotateKeys() {
        this.KEYS = await this.pu.generateKeyPair()
    }
}
