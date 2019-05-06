import { KeyPair, KeyType } from 'libsodium-wrappers'
import { PackUnpack } from 'pack-unpack'
import { Wallet } from './wallet'

export interface ISerializedBrowserKeyPair {
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
    private Wallet: Wallet = new Wallet()

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
     * Stores the agent public key, key can be either serialized or deserialzed
     * If a key currently exists, the key is automatically rotated to the new key
     * @param agentKey: ISerializedKey | IDeSerializedKey
     */
    public setAgentKey(agentKey: number[] | Uint8Array ) {
        if (agentKey instanceof Uint8Array) {
            agentKey = Array.from(agentKey)
        }
        this.Wallet.setAgentKey(agentKey)
    }

    /**
     * Returns a Deserialized key keybuffer for the agent public key
     */
    public getAgentKeyDeserialized(): Uint8Array | undefined {
        const keys = this.Wallet.retrieveAgentKey()
        return !keys ? keys : Uint8Array.from(keys)
    }

    /**
     * Returns a serialized agent public key
     */
    public getAgentKeySerialized(): number[] | undefined {
        return this.Wallet.retrieveAgentKey()
    }

    /**
     * Returns a sodium keypair
     * If no keys exist in the wallet a new keypair is automatically generated.
     * This allows you to call for the keys even if they do not exist.
     */
    public async getKeyPairDeserialized(): Promise<KeyPair> {
        return this.deserializeKeys(await this.getKeys())
    }

    /**
     * Returns a serialized sodium keypair
     * If no keys exist in the wallet a new keypair is automatically generated.
     * This allows you to call for the keys even if they do not exist.
     */
    public async getKeyPairSerialized(): Promise<ISerializedBrowserKeyPair> {
        return await this.getKeys()
    }

    /**
     * Returns the sodium public personal key
     * If no keys exist in the wallet a new keypair is automatically generated.
     * This allows you to call for the keys even if they do not exist.
     */
    public async getPersonalPubKeyDeserialized(): Promise<Uint8Array> {
        const keys = await this.getKeys()
        return this.deserializeKeys(keys).publicKey
    }

    /**
     * Returns the serialized public personal key
     * If no keys exist in the wallet a new keypair is automatically generated.
     * This allows you to call for the keys even if they do not exist.
     */
    public async getPersonalPubKeySerialized(): Promise<number[]> {
        const keys = await this.getKeys()
        return keys.publicKey
    }

    /**
     * Generates a new libsodium key and stores it. To retrieve the new keys await this method
     * then call a GET method for the keys exposed in this class
     */
    public async rotateKeyPair() {
        const newKeys = await this.PackUnpack.generateKeyPair()
        this.Wallet.setBrowserKeys(this.serializeKeys(newKeys))
    }

    /**
     * Removes all keys from the wallet
     */
    public deleteWallet() {
        this.Wallet.deleteWallet()
    }

    private async getKeys(): Promise<ISerializedBrowserKeyPair> {
        const keys = this.Wallet.retrieveBrowserKeys()
        if (!keys) {
            const newKeys = await this.PackUnpack.generateKeyPair()
            this.Wallet.setBrowserKeys(this.serializeKeys(newKeys))
            return this.serializeKeys(newKeys)
        }
        return keys
    }

    private deserializeKeys(keys: ISerializedBrowserKeyPair): KeyPair {
        return {
            keyType: keys.keyType,
            privateKey: Uint8Array.from(keys.privateKey),
            publicKey: Uint8Array.from(keys.publicKey),
        }
    }

    private serializeKeys(keys: KeyPair): ISerializedBrowserKeyPair {
        return {
            keyType: keys.keyType,
            privateKey: Array.from(keys.privateKey),
            publicKey: Array.from(keys.publicKey),
        }
    }
}
