import * as vcx from 'node-vcx-wrapper'

export interface IConnectionStore {
    [key: string]: vcx.Connection
}

export interface IStore {
    Connection: IConnectionStore
}

export class DataStore {

    private store: IStore = {
        Connection: {},
    }

    public setConnection(did: string, connection: vcx.Connection) {
        this.store.Connection[did] = connection
    }

    public getConnection(did: string) {
        try {
            return this.store.Connection[did]
        } catch (e) {
            return 'No connection with that DID exists'
        }
    }
}
