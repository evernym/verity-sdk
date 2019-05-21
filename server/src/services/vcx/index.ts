import fs = require('fs')
import * as vcx from 'node-vcx-wrapper'

export class Vcx {

    constructor() {
        console.log('vcx')
    }

    public async init() {
        try {
            console.log(this.getConfig())
            await vcx.initVcxWithConfig(this.getConfig())
            vcx.defaultLogger('warn')
        } catch (e) {
            throw e
        }
    }

    private getConfig(): string {
        const config = fs.readFileSync('/etc/verity-server/vcxconfig.json').toString()
        // read sdk_to_remote_did
        // read sdk_to_remote_verkey
        // set Agency.myDID to sdk_to_remote_did
        // set Agency.myVK = sdk_to_remote_verkey
        return config
    }
}
