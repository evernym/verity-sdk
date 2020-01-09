module.exports = class Context {
    constructor(configString) {
        config = JSON.parse(configString)
        Object.assign(this, config)
        this.walletConfig = JSON.stringify({id: config.walletName})
        this.walletCredentials = JSON.stringify({key: config.walletKey})
    }
}