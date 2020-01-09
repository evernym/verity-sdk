const expect = require('chai').expect
const Context = require('../src/Context')

describe('Context', () => {
    it('should have all attributes from configurationString', () => {
        config = {
            walletName: 'test-wallet',
            walletKey: 'wallet-key'
        }
        context = new Context(JSON.stringify(config))
        expect(context.walletName).to.equal(config.walletName)
        expect(context.walletKey).to.equal(config.walletKey)
    })

    it('should validate configuration', () => {
        validConfig = {
            
        }
    })
})