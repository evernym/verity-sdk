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
            vcx.defaultLogger('trace')
        } catch (e) {
            throw e
        }
    }

    private getConfig(): string {
        const config = fs.readFileSync('/etc/verity-server/vcxconfig.json').toString()
        return config
    }
}
