import * as vcx from 'node-vcx-wrapper'

export interface IDataStore {
    Connection: { [key: string]: vcx.Connection },
    CredentialDef: { [key: string]: vcx.CredentialDef }
}

export class DataStore {

    private store: IDataStore = {
        Connection: {},
        CredentialDef: {},
    }

    public setConnection(id: string, connection: vcx.Connection) {
        this.store.Connection[id] = connection
    }

    public getConnection(id: string): vcx.Connection | undefined {
        try {
            return this.store.Connection[id]
        } catch (e) {
            return undefined
        }
    }

    public async setCredentialDef(credDef: vcx.CredentialDef) {
        const id = await credDef.getCredDefId()
        this.store.CredentialDef[id] = credDef
    }

    public getCredentialDef(credDefId: string): vcx.CredentialDef | undefined {
        try {
            return this.store.CredentialDef[credDefId]
        } catch (e) {
            return undefined
        }
    }
}
