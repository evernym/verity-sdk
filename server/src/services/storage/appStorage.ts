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
}
