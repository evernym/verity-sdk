import * as _sodium from 'libsodium-wrappers'
import { KeyManager } from '.'

describe('Key manager', () => {

    it('is an async constructor', async () => {
        const keyManager = new KeyManager()
        const unresolvedVal = keyManager.Ready
        expect(unresolvedVal).toBeInstanceOf(Promise)
        const val = await keyManager.Ready
        expect(val).toEqual(undefined)
    })

    it('returns me a key pair from storage', async () => {
        const keyMgr = new KeyManager()
        await keyMgr.Ready
        const keys = await keyMgr.getKeyPair()
        expect(keys.keyType).toEqual('ed25519')
        expect(keys.privateKey).toBeInstanceOf(Uint8Array)
        expect(keys.publicKey).toBeInstanceOf(Uint8Array)
    })

    it('generates me a new keypair and returns it from storage', async () => {
        const keyMgr = new KeyManager()
        await keyMgr.Ready
    })

    it('stores an agent public key and returns the key from storage', async () => {
        const keyMgr = new KeyManager()
        await keyMgr.Ready
    })

    it('returns a json formated public key from storage', async () => {
        const keyMgr = new KeyManager()
        await keyMgr.Ready
    })

    it('rotates my keys, stores them, and returns them', async () => {
        const keyMgr = new KeyManager()
        await keyMgr.Ready
    })
})
