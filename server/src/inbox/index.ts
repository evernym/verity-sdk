import { KeyPair } from 'libsodium-wrappers';
import { PackUnpack } from 'pack-unpack';
import { protocolExtensionRouter } from '../protocol-extensions';
import { Response } from 'express';

export class Inbox {

    private packUnpack: PackUnpack = new PackUnpack()
    private setup: boolean

    constructor() {
        this.setup = false
    }

    public async newMessage(message: string, keypair: KeyPair, _sseHandle: Response, APK: Uint8Array) {
        if (!this.setup) {
            await this.packUnpack.setup()
            this.setup = true
        }

        const unpackedMessage = await this.packUnpack.unpackMessage(message, keypair)
        const msg = JSON.parse(unpackedMessage.message)
        protocolExtensionRouter(msg, _sseHandle, keypair, APK)
    }
}