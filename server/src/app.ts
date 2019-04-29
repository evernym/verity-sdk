import express = require('express')
import { PaymentRuntime } from './services/libnullpay'
import { Vcx } from './services/vcx'
import { initialiseSSE } from './transport/sse'
import * as _sodium from 'libsodium-wrappers'
import { KeyManager } from './services/key-management';
import bodyParser = require('body-parser');
import { Inbox } from './inbox';

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

    const inbox = new Inbox()

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
        await initialiseSSE(req, res)
        sseRes = res
        res.write('data: SSE_ESTABLISHED\n\n')
    })

    app.post('/msg', async (req, _res) => {
        console.log(`new inboxed message:`, req.body)
        inbox.newMessage(req.body.msg, KM.returnKeys(), sseRes, sseKeys)
        sseRes.write(`data: 200: Successful mailbox delivery\n\n`)
    })

    app.listen(port, () => console.log(`express server has started and is listening on port ${port}`))
}).catch(e => {
    
    console.log('Services NOT started! Error: ', e)
})
