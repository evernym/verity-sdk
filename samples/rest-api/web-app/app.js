'use strict'

// VAS = Verity Application Server
// B/E = Back end (NodeJS Express web server defined in app.js)
// F/E = Front end (static Vue.js HTML page defined in public/index.html)

const axios = require('axios')
const bodyParser = require('body-parser')
const express = require('express')
const http = require('http')
const QR = require('qrcode')
const socketIO = require('socket.io')
const uuid4 = require('uuid4')
const readline = require('readline')

const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const PORT = 3000
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

// Stores IO sessions established between F/E and B/E
const sessions = {}

let verityUrl // address of VAS
let xApiKey // REST API key associated with Domain DID
let domainDid
let webhookUrl // public URL for the started Ngrok tunnel to localhost port 3000

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

// This function will prompt for necessary input parameters (if parameters are not set via environment variables)
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

// Validates that VAS URL is correct by sending a request to the /agency route
// VAS should respond with DID and verKey to such request
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

// Sends Verity REST API call to VAS
// Full URL address for the REST API call is dinamycially constructed based on parameters sent from F/E
// Field @type is dinamycially constructed based on parameters sent from F/E and added to the message payload
async function sendVerityRESTMessage (data) {
  // If Message Family Qualifier is not specified in parameters sent from F/E, assume Evernym one
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
      'X-API-key': xApiKey // <-- REST API Key is added in the header
    }
  })
}

async function main () {
  const app = express()
  const server = http.createServer(app)

  // IO sockets are used to transfer messages between F/E and B/E
  // For instance, response messages from VAS sent to webhook URL are transfered from B/E to F/E so that they can be shown on the designated area in F/E
  const io = socketIO(server)
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

  await readInputParameters()

  app.use(bodyParser.json())

  // Serve static files contained in public folder
  app.use(express.static('public'))

  app.get('/uuid', (req, res) => {
    res.send(uuid4())
  })

  // F/E calls this route to get VerityURL and domain DID so that they can be shown in the Configuration section on F/E
  app.get('/getConfig', async (req, res) => {
    res.status(200).send({ verityUrl, domainDid })
  })

  // F/E gets webhook URL for the session by calling this route
  // Webhook Url is constructed from the public address of ngrok tunnel started for localhost port 3000 and sessionId
  app.post('/webhookUrl', async (req, res) => {
    try {
      res.send(getWebhookUrl(req.body.sessionId))
    } catch (err) {
      res.status(500).send()
    }
  })

  // VAS will send REST API callbacks to the webhook URL
  // B/E will receive response on this route and transfer response message to the appropriate F/E session via IO sockets
  // F/E will show recieved webhook URL message in the designated area on F/E
  app.post('/webhook/:sessionId', async (req, res) => {
    const sessionId = req.params.sessionId
    console.log(`Got message for session ${sessionId}`)
    console.log(req.body)
    if (sessionId in sessions) {
      // Send response message to F/E via IO socket
      sessions[sessionId].socket.emit('message', req.body)
      res.status(200).send('Accepted')
    } else {
      res.status(404).send(`Webhook with id ${sessionId} not found`)
    }
  })

  // F/E will call this route to initiate REST API calls
  // B/E will prepare REST API call based on data from F/E and send it to Verity server
  app.post('/send', async (req, res) => {
    try {
      await sendVerityRESTMessage(req.body)
      res.status(202).send('Accepted')
    } catch (e) {
      console.log(e)
      res.status(500).send()
    }
  })

  // F/E posts invite URL to B/E and recieves the generated QR code which is then displayed on F/E
  app.post('/qr', async (req, res) => {
    try {
      res.send(await QR.toDataURL(req.body.url))
    } catch (err) {
      res.status(500).send()
    }
  })

  // This will register Issuer DID/Verkey as Endorser on Sovrin Staging Net via Sovrin portal
  app.post('/registerDID', async (req, res) => {
    try {
      const response = await axios.post('https://selfserve.sovrin.org/nym', req.body)
      res.status(200).send(response.data.body)
    } catch (err) {
      res.status(500).send(err)
    }
  })

  server.listen(PORT, () => {
    console.log(`Listening on port ${PORT}`)
  })
}

main()
