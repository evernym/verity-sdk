import { KeyPair } from 'libsodium-wrappers';
import { PackUnpack } from 'pack-unpack';
import { protocolExtensionRouter } from '../protocol-extensions'
import { Response } from 'express';
import { Agency } from '../services/agency/register-agent'

export interface IProtocols {
    agency: Agency
}

export class Inbox {

    private packUnpack: PackUnpack = new PackUnpack()
    private setup: boolean

    constructor() {
        this.setup = false
    }

    public async newMessage(message: string, keypair: KeyPair, _sseHandle: Response, _APK: Uint8Array, protocols: IProtocols) {
        if (!this.setup) {
            await this.packUnpack.Ready
            this.setup = true
        }

        const unpackedMessage = await this.packUnpack.unpackMessage(message, keypair)
        const msg = JSON.parse(unpackedMessage.message)
        protocolExtensionRouter(msg, protocols)
    }
}