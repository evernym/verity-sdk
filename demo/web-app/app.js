'use strict'

// Imports
const axios = require('axios')
const bodyParser = require('body-parser')
const express = require('express')
const http = require('http')
const QR = require('qrcode')
const socketIO = require('socket.io')
const uuid4 = require('uuid4')
const readline = require('readline')

// Constants
const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const PORT = 3000
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

const sessions = {}

let verityUrl
let xApiKey
let domainDid
let webhookUrl

function getWebhookUrl (sessionId) {
  return webhookUrl + '/webhook/' + sessionId
}

async function readlineInput (request, defaultValue) {
  return new Promise((resolve) => {
    if (defaultValue) {
      resolve(defaultValue)
    } else {
      rl.question(request + ': ', (response) => { resolve(response) })
    }
  })
}

async function readInputParameters () {
  verityUrl = await readlineInput('Verity Application URL', process.env.VERITY_URL)
  if ((await validateVerityUrl(verityUrl)) === 'Invalid') {
    console.log('Invalid Verity Url')
    process.exit(1)
  }
  domainDid = await readlineInput('Domain DID', process.env.DOMAIN_DID)
  if (domainDid.length < 22 || domainDid.length > 23) {
    console.log('Invalid DID')
    process.exit(1)
  }
  xApiKey = await readlineInput('X-API-KEY', process.env.X_API_KEY)
  webhookUrl = await readlineInput('Webhook URL', process.env.WEBHOOK_URL)
  console.log()
  console.log('----------------------------------------------------------------------------------')
  console.log(`Verity Application URL: ${ANSII_GREEN}${verityUrl}${ANSII_RESET}`)
  console.log(`Domain DID: ${ANSII_GREEN}${domainDid}${ANSII_RESET}`)
  console.log(`X-API-KEY: ${ANSII_GREEN}${xApiKey}${ANSII_RESET}`)
  console.log(`Webhook URL: ${ANSII_GREEN}${webhookUrl}${ANSII_RESET}`)
  console.log('----------------------------------------------------------------------------------')
  console.log()
}

async function validateVerityUrl (verityUrl) {
  try {
    const response = await axios.get(verityUrl + '/agency')
    if (response.data.DID && response.data.verKey) {
      return 'Valid'
    } else {
      return 'Invalid'
    }
  } catch (err) {
    return 'Invalid'
  }
}

async function sendVerityRESTMessage (data) {
  const qualifier = data.qualifier ? data.qualifier : '123456789abcdefghi1234'
  data.message['@type'] = `did:sov:${qualifier};spec/${data.msgFamily}/${data.msgFamilyVersion}/${data.msgName}`
  const url = verityUrl + '/api/' + domainDid + '/' + data.msgFamily + '/' + data.msgFamilyVersion + '/' + data.threadId
  console.log(`Posting message to ${url}`)
  console.log(data.message)
  return axios({
    method: 'POST',
    url: url,
    data: data.message,
    headers: {
      'X-API-key': xApiKey
    }
  })
}

async function main () {
  const app = express()
  const server = http.createServer(app)
  const io = socketIO(server)

  await readInputParameters()

  app.use(bodyParser.json())
  app.use(express.static('public'))

  app.get('/uuid', (req, res) => {
    res.send(uuid4())
  })

  app.get('/getConfig', async (req, res) => {
    res.status(200).send({ verityUrl, domainDid })
  })

  app.post('/webhookUrl', async (req, res) => {
    try {
      res.send(getWebhookUrl(req.body.sessionId))
    } catch (err) {
      res.status(500).send()
    }
  })

  app.post('/webhook/:sessionId', async (req, res) => {
    const sessionId = req.params.sessionId
    console.log(`Got message for session ${sessionId}`)
    console.log(req.body)
    if (sessionId in sessions) {
      sessions[sessionId].socket.emit('message', req.body)
      res.status(200).send('Accepted')
    } else {
      res.status(404).send(`Webhook with id ${sessionId} not found`)
    }
  })

  app.post('/send', async (req, res) => {
    try {
      await sendVerityRESTMessage(req.body)
      res.status(202).send('Accepted')
    } catch (e) {
      console.log(e)
      res.status(500).send()
    }
  })

  app.post('/qr', async (req, res) => {
    try {
      res.send(await QR.toDataURL(req.body.url))
    } catch (err) {
      res.status(500).send()
    }
  })

  app.post('/registerDID', async (req, res) => {
    try {
      const response = await axios.post('https://selfserve.sovrin.org/nym', req.body)
      res.status(200).send(response.data.body)
    } catch (err) {
      res.status(500).send(err)
    }
  })

  io.on('connection', (socket) => {
    console.log('user connected')
    socket.on('disconnect', () => {
      console.log('user disconnected')
    })

    socket.on('update', (data) => {
      sessions[data.sessionId] = {
        socket: socket
      }
      console.log('Socket session updated')
    })
  })

  server.listen(PORT, () => {
    console.log(`Listening on port ${PORT}`)
  })
}

main()
