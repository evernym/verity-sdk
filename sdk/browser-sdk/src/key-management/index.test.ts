import * as _sodium from 'libsodium-wrappers'
import { ISerializedBrowserKeyPair, KeyManager } from '.'
import { Wallet } from './wallet'

describe('Key manager', () => {

    const keyMgr = new KeyManager()

    const testSerializedAgentPK = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]
    const testDeSerializedAgentPK = Uint8Array.from(testSerializedAgentPK)

    const testSerializedBrowserKeys: ISerializedBrowserKeyPair = {
        keyType: 'ed25519',
        privateKey: [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],
        publicKey: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    }

    const testDeerializedBrowserKeys: _sodium.KeyPair = {
        keyType: 'ed25519',
        privateKey: Uint8Array.from([2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2]),
        publicKey: Uint8Array.from([1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1]),
    }

    beforeEach( async () => {
        await keyMgr.Ready
        localStorage.clear()
    })

    it('is an async constructor', async () => {
        const keyManager = new KeyManager()
        const unresolvedVal = keyManager.Ready
        expect(unresolvedVal).toBeInstanceOf(Promise)
        const val = await keyManager.Ready
        expect(val).toEqual(undefined)
    })

    it('stores an agent public key - serialized', async () => {
        keyMgr.setAgentKey(testSerializedAgentPK)
        const keysFromStorage = localStorage.getItem('AGENT_PUBLIC_KEY')
        keysFromStorage ? expect(JSON.parse(keysFromStorage)).toEqual(testSerializedAgentPK) : fail()
    })

    it('stores an agent public key - deserialized', async () => {
        keyMgr.setAgentKey(testDeSerializedAgentPK)
        const keysFromStorage = localStorage.getItem('AGENT_PUBLIC_KEY')
        keysFromStorage ? expect(JSON.parse(keysFromStorage)).toEqual(testSerializedAgentPK) : fail()
    })

    it('returns the deserialized public agent key, if none exists returns undefined', async () => {
        expect(keyMgr.getAgentKeyDeserialized()).toBeUndefined()
        keyMgr.setAgentKey(testSerializedAgentPK)
        expect(keyMgr.getAgentKeyDeserialized()).toEqual(testDeSerializedAgentPK)
    })

    it('returns the serialized public agent key, if none exists returns undefined', async () => {
        expect(keyMgr.getAgentKeySerialized()).toBeUndefined()
        keyMgr.setAgentKey(testSerializedAgentPK)
        expect(keyMgr.getAgentKeySerialized()).toEqual(testSerializedAgentPK)
    })

    it('returns the deserialized keypair', async () => {
        const wallet = new Wallet()
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        expect(await keyMgr.getKeyPairDeserialized()).toEqual(testDeerializedBrowserKeys)
    })

    it('returns the serialized keypair', async () => {
        const wallet = new Wallet()
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        expect(await keyMgr.getKeyPairSerialized()).toEqual(testSerializedBrowserKeys)
    })

    it('returns a new deserialed keypair if storage empty', async () => {
        const keys = await keyMgr.getKeyPairDeserialized()
        expect(keys.publicKey instanceof Uint8Array).toEqual(true)
    })

    it('returns new serialized keypair if storage empty', async () => {
        const keys = await keyMgr.getKeyPairSerialized()
        expect(keys.publicKey instanceof Array).toEqual(true)
    })

    it('returns the deserialized personal public key ', async () => {
        const wallet = new Wallet()
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        expect(await keyMgr.getPersonalPubKeyDeserialized()).toEqual(testDeerializedBrowserKeys.publicKey)
    })

    it('returns the serialized personal public key', async () => {
        const wallet = new Wallet()
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        expect(await keyMgr.getPersonalPubKeySerialized()).toEqual(testSerializedBrowserKeys.publicKey)
    })

    it('returns a new deserialized personal public key if storage empty', async () => {
        const keys = await keyMgr.getPersonalPubKeyDeserialized()
        expect(keys instanceof Uint8Array).toEqual(true)
    })

    it('returns a new serialized personal public key if storage empty', async () => {
        const keys = await keyMgr.getPersonalPubKeySerialized()
        expect(keys instanceof Array).toEqual(true)
    })

    it('rotates my keypair', async () => {
        const wallet = new Wallet()
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        await keyMgr.rotateKeyPair()
        const keys = await keyMgr.getKeyPairSerialized()
        expect(keys.publicKey !== testSerializedBrowserKeys.publicKey).toBeTruthy()
    })

    it('rotates the agent key', async () => {
        keyMgr.setAgentKey([0])
        const keysFromStorage = localStorage.getItem('AGENT_PUBLIC_KEY')
        keysFromStorage ? expect(JSON.parse(keysFromStorage)).toEqual([0]) : fail()
        keyMgr.setAgentKey(testSerializedAgentPK)
        expect(keyMgr.getAgentKeySerialized()).toEqual(testSerializedAgentPK)
    })

    it('deletes the wallet storage', async () => {
        await keyMgr.getKeyPairSerialized()
        keyMgr.deleteWallet()
        expect(localStorage.getItem('PERSONAL_KEY_PAIR')).toBeFalsy()
    })
})
