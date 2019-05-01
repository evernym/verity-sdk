import { ISerializedAgentKey, ISerializedBrowserKeyPair, Wallet } from '.'

describe('key-management wallet', () => {

    const testSerializedBrowserKeys: ISerializedBrowserKeyPair = {
        keyType: 'ed25519',
        privateKey: [2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2],
        publicKey: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    }

    const testSerializedAgentPK: ISerializedAgentKey = {
        publicKey: [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1],
    }

    const wallet = new Wallet()

    beforeEach(() => {
        localStorage.clear()
    })

    it('stores a browser key', () => {
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        const keys = localStorage.getItem('PERSONAL_KEY_PAIR')
        if (keys) {
            expect(JSON.parse(keys)).toEqual(testSerializedBrowserKeys)
        } else {
            fail()
        }
    })

    it('stores a public key', () => {
        wallet.setAgentKey(testSerializedAgentPK)
        const keys = localStorage.getItem('AGENT_PUBLIC_KEY')
        if (keys) {
            expect(JSON.parse(keys)).toEqual(testSerializedAgentPK)
        } else {
            fail()
        }
    })

    it ('returns undefined if no key exists, or a serialized browser keypair', () => {
        wallet.setBrowserKeys(testSerializedBrowserKeys)
        const keys = wallet.retrieveBrowserKeys()
        expect(keys).toEqual(testSerializedBrowserKeys)
        const noKeys = wallet.retrieveAgentKey()
        expect(noKeys).toEqual(undefined)
    })

    it('returns undefined if no key exists, or a serialized agent pk', () => {
        wallet.setAgentKey(testSerializedAgentPK)
        const keys = wallet.retrieveAgentKey()
        expect(keys).toEqual(testSerializedAgentPK)
        const noKeys = wallet.retrieveBrowserKeys()
        expect(noKeys).toEqual(undefined)
    })
})
