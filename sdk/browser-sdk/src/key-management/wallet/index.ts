import { ISerializedBrowserKeyPair } from '..'

/**
 *
 * Verity SDK wallet is a web storage mechanism for persisting keys to the browser. Due to the
 * nature of cross site attacks in the browser, this wallet is NOT intended for long term storage
 * of cryptographic keys. Keys are protected by the cross origin policy outlined here:
 * https://www.w3.org/Security/wiki/Same_Origin_Policy which guarantees some safety.
 *
 * The verity-sdk browser wallet is capable of storing only two keys at a time:
 * 1. The personal LibSodium KeyPair containing the private, public, and keytype
 * 2. The Public key of the verity-application
 *
 * Verity application keys ARE ephemeral and as such when sessions end with the verity
 * application keys expire. Even though the keys can expire they may remain in this wallet
 * so long as the browsers database has not been cleared. If the database is cleared the keys
 * are lost and new authentication with the verity-application will need to occur.
 */
export class Wallet {

    private readonly KeyValues = {
        agentPK: 'AGENT_PUBLIC_KEY',
        browserKeyPair: 'PERSONAL_KEY_PAIR',
    }

    public setBrowserKeys(keys: ISerializedBrowserKeyPair) {
        localStorage.setItem(this.KeyValues.browserKeyPair, JSON.stringify(keys))
    }

    public setAgentKey(key: number[]) {
        localStorage.setItem(this.KeyValues.agentPK, JSON.stringify(key))
    }

    public retrieveBrowserKeys(): ISerializedBrowserKeyPair | undefined {
        const k = localStorage.getItem(this.KeyValues.browserKeyPair)
        if (k) {
            return JSON.parse(k)
        } else {
            return undefined
        }
    }

    public retrieveAgentKey(): number[] | undefined {
        const k = localStorage.getItem(this.KeyValues.agentPK)
        if (k) {
            return JSON.parse(k)
        } else {
            return undefined
        }
    }

    public deleteWallet() {
        localStorage.clear()
    }
}
