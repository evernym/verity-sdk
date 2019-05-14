import express = require('express')
import { PaymentRuntime } from './services/libnullpay'
import { Vcx } from './services/vcx'
import { initializeSSE } from './transport/sse'
import * as _sodium from 'libsodium-wrappers'
import { KeyManager } from './services/key-management';
import bodyParser = require('body-parser');
import { Inbox } from './inbox';
import { Agency } from './services/agency/register-agent'

async function startServices() {
    try {
        const nullPay = new PaymentRuntime()
        await nullPay.initPayment()

        const vcx = new Vcx()
        await vcx.init()

    } catch (e) {
        throw e
    }
}

startServices().then(async () => {
    const KM = new KeyManager()
    await KM.setup()

    const agency = new Agency()
    const inbox = new Inbox()

    const protocols = {
        agency
    }

    let sseRes: any = undefined
    let sseKeys = new Uint8Array

    console.log('Services successfully started')
    const app = express()
    const port = 8080

    app.use(bodyParser.json())
    app.use(bodyParser.urlencoded({
        extended: true
    })); 
    app.use(function(_req, res, next) {
        res.header("Access-Control-Allow-Origin", "*");
        res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        next();
    });

    app.post('/handshake', async (_req, res) => {
        sseKeys = Uint8Array.from(_req.body)
        console.log('sseKeys', sseKeys)
        res.json(KM.returnPublicKeyTransport())
    })

    app.get('/sse-handshake', async (req, res) => {
        console.log('new connection has requested sse stream !', req.ip)
        await initializeSSE(req, res)
        sseRes = res
        const response = JSON.stringify({ msg: 'SSE_ESTABLISHED', status: 0 })
        res.write(`data: ${response}\n\n`)
    })

    app.post('/msg', async (req, _res) => {
        console.log(`new inboxed message:`, req.body)
        inbox.newMessage(req.body.msg, KM.returnKeys(), sseRes, sseKeys, protocols)
        _res.sendStatus(200)
    })

    app.get('/agency', async (_req, res) => {
        res.send({ DID: agency.config.myDID, verKey: agency.config.myVerkey })
    })

    app.listen(port, () => console.log(`express server has started and is listening on port ${port}`))
}).catch(e => {
    
    console.log('Services NOT started! Error: ', e)
})
