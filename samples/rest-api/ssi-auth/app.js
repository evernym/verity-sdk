const express = require('express')
const axios = require('axios')
const http = require('http')
const urljoin = require('url-join')
const session = require('express-session')
const socketIO = require('socket.io')
const QR = require('qrcode')
const uuid4 = require('uuid4')
const readline = require('readline')
const bcrypt = require('bcrypt')

const saltRounds = 10
const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const PORT = 3000
const rl = readline.createInterface({
  input: process.stdin,
  output: process.stdout
})

let verityUrl // address of Verity Application Service (VAS)
let xApiKey // REST API key associated with Domain DID
let domainDid
let webhookUrl // public URL for the started Ngrok tunnel to the application port (localhost:3000)

let webhookResolve // Update webhook protocol is synchronous and does not support threadId

// Sends a message to the Verity Application Service via the Verity REST API
async function sendVerityRESTMessage (qualifier, msgFamily, msgFamilyVersion, msgName, message, threadId) {
  // qualifier - either 'BzCbsNYhMrjHiqZDTUASHg' for Aries community protocols or '123456789abcdefghi1234' for Evernym-specific protocols
  // msgFamily - message family (e.g. 'present-proof')
  // msgFamilyVersion - version of the message family (e.g. '1.0')
  // msgName - name of the protocol message to perform (e.g. 'request')
  // message - message to be sent in the body payload
  // threadId - unique identifier of the protocol interaction. The threadId is used to distinguish between simultaenous interactions

  // Add @type and @id fields to the message in the body payload
  // Field @type is dinamycially constructed from the function arguments and added into the message payload
  message['@type'] = `did:sov:${qualifier};spec/${msgFamily}/${msgFamilyVersion}/${msgName}`
  message['@id'] = uuid4()

  if (!threadId) {
    threadId = uuid4()
  }

  // send prepared message to Verity and return Axios request promise
  const url = urljoin(verityUrl, 'api', domainDid, msgFamily, msgFamilyVersion, threadId)
  console.log(`Posting message to ${ANSII_GREEN}${url}${ANSII_RESET}`)
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
  return axios({
    method: 'POST',
    url: url,
    data: message,
    headers: {
      'X-API-key': xApiKey // <-- REST API Key is added in the header
    }
  })
}

// Helper function to prompt the user for the input parameter
async function readlineInput (request, defaultValue) {
  return new Promise((resolve) => {
    if (defaultValue) {
      resolve(defaultValue)
    } else {
      rl.question(request + ': ', (response) => { resolve(response) })
    }
  })
}

// This function will prompt for all necessary input parameters (if parameters are not already set via environment variables)
async function readInputParameters () {
  verityUrl = await readlineInput('Verity Application URL', process.env.VERITY_URL)
  if ((await validateVerityUrl(verityUrl)) === 'Invalid') {
    console.log('Invalid Verity Url')
    process.exit(1)
  }
  domainDid = await readlineInput('Domain DID', process.env.DOMAIN_DID)
  if (domainDid.length < 21 || domainDid.length > 22) {
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
// VAS should respond with DID and verKey to a such request
async function validateVerityUrl (verityUrl) {
  try {
    const response = await axios.get(verityUrl + '/agency')
    if (response.data.DID && response.data.verKey) {
      return 'Valid'
    } else {
      return 'Invalid'
    }
  } catch (err) {
    console.log(err)
    return 'Invalid'
  }
}

// generate N random numbers in the interval [min,max) without duplicates
// this is used to generate choices for the 2FA challenge
function generateChallenges (n, min, max) {
  const challenges = []
  for (let i = 0; i < n; i++) {
    const temp = min + Math.floor(Math.random() * (max - min))
    if (challenges.indexOf(temp) === -1) {
      challenges.push(temp)
    } else { i-- }
  }
  return challenges
}

// This function creates a new relationship invitation and returns created relationshipDid and inviteUrl
// inviteUrl is later converted to the QR code and scanned by the user's wallet app
async function createInvitation () {
  const relationshipCreateMessage = {}
  const relThreadId = uuid4()
  const relationshipCreate =
   new Promise(function (resolve, reject) {
     relCreateResolveMap.set(relThreadId, resolve)
     sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'create', relationshipCreateMessage, relThreadId)
   })
  const relationshipDid = await relationshipCreate

  // create relationship invitation using the Out-of-Band protocol
  const relationshipInvitationMessage = {
    '~for_relationship': relationshipDid
  }
  const relationshipInvitation =
   new Promise(function (resolve, reject) {
     relInvitationResolveMap.set(relThreadId, resolve)
     sendVerityRESTMessage('123456789abcdefghi1234', 'relationship', '1.0', 'out-of-band-invitation', relationshipInvitationMessage, relThreadId)
   })
  const [inviteUrl, invitationId] = await relationshipInvitation
  inviteToDidMap.set(invitationId, relationshipDid)

  return [relationshipDid, inviteUrl]
}

// This function waits until the user accepts connection invite (Connection promise is resolved)
// or the timeout has occurred (Timeout promise is resolved)
// It returns connection status ['accepted', 'redirected' or 'timeout'] and redirectDID
async function waitConnectionAccepted (relationshipDid) {
  const ConnectionPromise = new Promise(function (resolve, reject) {
    connectionResolveMap.set(relationshipDid, resolve)
  })

  // wait maximum for 2 minutes for the user to scan the QR code
  const TimeoutPromise = new Promise(resolve => {
    setTimeout(() => {
      resolve(['timeout', null])
    }, 2 * 60 * 1000)
  })

  // wait for the connection promise or for the timeout promise to resolve
  // whatever happens first
  const [status, redirectDID] = await Promise.race([
    ConnectionPromise,
    TimeoutPromise
  ])

  return [status, redirectDID]
}

async function initialize () {
  // Update webhook endpoint
  const webhookMessage = {
    comMethod: {
      id: 'webhook',
      type: 2,
      value: webhookUrl,
      packaging: {
        pkgType: 'plain'
      }
    }
  }

  const updateWebhook =
  new Promise(function (resolve, reject) {
    webhookResolve = resolve
    sendVerityRESTMessage('123456789abcdefghi1234', 'configs', '0.6', 'UPDATE_COM_METHOD', webhookMessage)
  })

  await updateWebhook

  // Update organization name and logo
  const updateConfigMessage = {
    configs: [
      {
        name: 'logoUrl',
        value: 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
      },
      {
        name: 'name',
        value: 'SSI Savvy Org'
      }
    ]
  }

  const updateConfigsThreadId = uuid4()
  const updateConfigs =
  new Promise(function (resolve, reject) {
    updateConfigsMap.set(updateConfigsThreadId, resolve)
  })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'update-configs', '0.6', 'update', updateConfigMessage, updateConfigsThreadId)

  await updateConfigs

  // Setup Issuer keys
  // It is needed to create issuer keys to support connection reuse scenario.
  // The Issuer DID is included in the field "public_did" of the connection invitation
  // and is used on the Holder's side to determine if it is already connected with the Inviter
  let issuerDid
  let issuerVerkey

  // check if Issuer Keys were already created
  const getIssuerKeysMsg = {}
  const getIssuerKeysThreadId = uuid4()

  const getIssuerKeys =
  new Promise(function (resolve, reject) {
    setupIssuerMap.set(getIssuerKeysThreadId, resolve)
  })

  await sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'current-public-identifier', getIssuerKeysMsg, getIssuerKeysThreadId);

  [issuerDid, issuerVerkey] = await getIssuerKeys

  if (issuerDid === undefined) {
    // if issuer Keys were not created, create Issuer keys
    const setupIssuerMsg = {}
    const setupIssuerThreadId = uuid4()
    const setupIssuer =
      new Promise(function (resolve, reject) {
        setupIssuerMap.set(setupIssuerThreadId, resolve)
      })

    await sendVerityRESTMessage('123456789abcdefghi1234', 'issuer-setup', '0.6', 'create', setupIssuerMsg, setupIssuerThreadId);
    [issuerDid, issuerVerkey] = await setupIssuer
    console.log(`Issuer DID: ${ANSII_GREEN}${issuerDid}${ANSII_RESET}`)
    console.log(`Issuer Verkey: ${ANSII_GREEN}${issuerVerkey}${ANSII_RESET}`)
  }
}

// Maps containing promises for the started interactions - threadId is used as the map key
// Update configs
const updateConfigsMap = new Map()
// Setup Issuer
const setupIssuerMap = new Map()
// Create relationship
const relCreateResolveMap = new Map()
// Relationship invitation
const relInvitationResolveMap = new Map()
// Stores connection promises. Relationship DID is used as the key
const connectionResolveMap = new Map()
// Stores 2FA question promises. threadId is used as the key
const questionResolveMap = new Map()

// Maps Out-of-band invitationId to the relationship DID
const inviteToDidMap = new Map()
// Maps relationship DID to the user's email
const didToEmailMap = new Map()
// Stores releationship DIDs for OoB connections for the 1st time user journeys
// i.e. where invite (QR code) is generated but account details are not yet provided
const pendingOobConnections = new Map()

// Stores data (name, email, password, did) about users. email is used as the key
// e.g.
// 'john.doe@abc.com' => {
//  email: 'john.doe@abc.com',
//  password: '$2b$10$H9BjKLKsZJpj7v5lb2B4NeR1WtfPrqFylMLIP93Snm9bdhvQ0gvVC',
//  name: 'John Doe',
//  did: '9QFjmQYDYcSA8ycyVDViHf'
// }
// This is in-memory map! When application is restarted all data is lost
// In real usage, user data should be stored in a persistent storage (e.g. DB)
const usersMap = new Map()

async function main () {
  const app = express()
  const server = http.createServer(app)

  // IO socket are used to send async notifications to F/E (like response messages from VAS)
  const io = socketIO(server)
  io.on('connection', (socket) => {
    console.log('user connected')
    socket.on('disconnect', () => {
      console.log('user disconnected')
    })
  })

  await readInputParameters()

  app.use(express.json())
  app.use(express.urlencoded({ extended: true }))
  app.use(session({ secret: 'Your secret key', saveUninitialized: 'false', resave: 'false' }))

  // This route handles registration requests for the 2FA use case
  app.post('/2fa_register', async function (req, res) {
    // Return error if the email already exists (email is used as the username)
    if (usersMap.has(req.body.email)) {
      res.status(400).send(`User with email ${req.body.email} already exist!`)
      return
    }

    // create a new object containing user data
    // user password is stored in a hashed form
    const newUser = {
      email: req.body.email,
      password: await bcrypt.hash(req.body.password, saltRounds),
      name: req.body.name,
      did: ''
    }

    // create a new relationship for the new user
    const [relationshipDid, inviteUrl] = await createInvitation()
    newUser.did = relationshipDid

    // send QR code containing inviteURL to the F/E
    io.to(req.body.socketId).emit('qrcode', await QR.toDataURL(inviteUrl))

    // Wait for user to accept the connection
    const [status, redirectDID] = await waitConnectionAccepted(relationshipDid)
    if (status === 'accepted') {
      // if connection was accepted store the new user object in the in-memory usersMap and set session headers
      usersMap.set(newUser.email, newUser)
      didToEmailMap.set(relationshipDid, newUser.email)
      req.session.user = newUser.email
      res.status(200).send('OK')
    }
    // send connection status notification to F/E
    io.to(req.body.socketId).emit('connection_response', { status, relationshipDid, redirectDID })
  })

  // This route handles login requests for the 2FA use case
  app.post('/2fa_login', async function (req, res) {
    const user = req.body.email

    // return an error if the specified user does not exist or the password provided is not matching the password registered with the user
    if (!(
      usersMap.has(user) &&
        'password' in usersMap.get(user) === true &&
        await bcrypt.compare(req.body.password, usersMap.get(user).password))
    ) {
      res.status(401).send('Incorrect username or password')
      return
    }

    // Generate 2FA challenge (5 random no-duplicates 4-digit numbers)
    const challenges = generateChallenges(5, 1000, 10000)
    // Set correct answer to be the first random number
    const correctAnswer = challenges[0]
    // change order of random numbers by sorting the random array so that the correct answer is not always in the first position on ConnectMe app
    const answers = challenges.sort()

    // Send notification with the correct answer to the F/E
    io.to(req.body.socketId).emit('2fa_challenge', correctAnswer)

    // Send 2FA challenge to the pairwise DID registered with the user
    const challengeQuestionMessage = {
      '~for_relationship': usersMap.get(user).did,
      text: 'Select the response',
      detail: '2FA',
      valid_responses: answers,
      signature_required: false
    }

    const questionThreadId = uuid4()
    const challengeQuestion =
    new Promise(function (resolve, reject) {
      questionResolveMap.set(questionThreadId, resolve)
      sendVerityRESTMessage('BzCbsNYhMrjHiqZDTUASHg', 'committedanswer', '1.0', 'ask-question', challengeQuestionMessage, questionThreadId)
    })

    // wait for the user to respond
    const answerGiven = await challengeQuestion

    if (parseInt(answerGiven) === correctAnswer) {
      // if the user responded with the correct answer set the session
      req.session.user = user
      res.status(200).send('OK')
      // notify the F/E that the user responded
      io.to(req.body.socketId).emit('answer_verified', {})
    } else {
      res.status(401).send('Incorrect response to challenge')
    }
  })

  // This route handles registration requests for the Out-of-band use case
  app.post('/oob_register', async function (req, res) {
    // check if the server sent Out-of-band invitation for the supplied DID
    if (pendingOobConnections.has(req.body.did)) {
      // create a new user object with the provided data
      const newUser = {
        email: req.body.email,
        name: req.body.name,
        did: req.body.did
      }
      // store user object in the in-memory Users map and set session headers
      usersMap.set(newUser.email, newUser)
      didToEmailMap.set(newUser.did, newUser.email)
      req.session.user = newUser.email
      // delete pending OoB connection, since it is now completed
      pendingOobConnections.delete(req.body.did)
      res.status(200).send('OK')
    } else {
      // Send error if the OoB registration form was sent with a crafted DID
      res.status(400).send(`Server does not expect registration details for DID ${req.body.did}`)
    }
  })

  // This route handles login requests for the Out-of-band use case
  app.post('/oob_login', async function (req, res) {
    // create a new invitation
    const [relationshipDid, inviteUrl] = await createInvitation()
    // send QR code containing inviteURL to the F/E
    io.to(req.body.socketId).emit('qrcode', await QR.toDataURL(inviteUrl))

    // Wait for user to scan the QR code
    let [status, redirectDID] = await waitConnectionAccepted(relationshipDid)

    if (status === 'accepted') {
      // This is a first-time user. Add DID to the pending connections
      // F/E will show a form to fill in user data when it receives "accepted" connection status
      pendingOobConnections.set(relationshipDid, req.body.socketId)
    }
    if (status === 'redirected') {
      // This is a returning user
      // Find which user is returning based on redirectDID
      if (didToEmailMap.has(redirectDID)) {
        // set session headers for the returning user
        req.session.user = didToEmailMap.get(redirectDID)
      } else {
        // set error status to the F/E, since a redirect DID could not be found in the Users in-memory map
        status = 'Redirect DID not found in the in-memory Users map'
      }
    }

    res.status(200).send('OK')

    // Notify F/E about connection status
    io.to(req.body.socketId).emit('connection_response', { status, relationshipDid, redirectDID })
  })

  // accountPage is a protected resource (requires authentication)
  app.get('/accountPage', function (req, res) {
    // Check if the user is authenticated (there is a session header set in the user's request)
    const user = usersMap.get(req.session.user)
    if (user) {
      // user is authenticated
      res.render('accountPage', { name: user.name, email: user.email, did: user.did })
    } else {
      // user is not authenticated
      // accountPage Pug view will show "Not logged in" if the email parameter is not passed in the render call
      res.render('accountPage', {})
    }
  })

  // On logout destroy session and redirect browser to the main page
  app.post('/logout', async function (req, res) {
    await req.session.destroy()
    res.redirect('/')
  })

  // On this route application will receive messages from VAS
  app.post('/', async (req, res) => {
    const message = req.body
    const threadId = message['~thread'] ? message['~thread'].thid : null
    const pthid = message['~thread'] ? message['~thread'].pthid : null
    console.log('Got message on the webhook')
    console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
    res.status(202).send('Accepted')

    // Handle received message differently based on the message type
    switch (message['@type']) {
      case 'did:sov:123456789abcdefghi1234;spec/configs/0.6/COM_METHOD_UPDATED':
        webhookResolve('webhook updated')
        break
      case 'did:sov:123456789abcdefghi1234;spec/update-configs/0.6/status-report':
        updateConfigsMap.get(threadId)('config updated')
        break
      case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier-created':
        setupIssuerMap.get(threadId)([message.identifier.did, message.identifier.verKey])
        break
      case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/problem-report':
        if (
          message.message === 'Issuer Identifier has not been created yet'
        ) {
          setupIssuerMap.get(threadId)([undefined, undefined])
        }
        break
      case 'did:sov:123456789abcdefghi1234;spec/issuer-setup/0.6/public-identifier':
        setupIssuerMap.get(threadId)([message.did, message.verKey])
        break
      case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/created':
      // Resolve relationship creation promise with the DID of the created relationship
        relCreateResolveMap.get(threadId)(message.did)
        break
      case 'did:sov:123456789abcdefghi1234;spec/relationship/1.0/invitation':
      // Resolve relationship invitation promise with inviteUrl and invitationId of the created OoB invitation
        relInvitationResolveMap.get(threadId)([message.inviteURL, message.invitationId])
        break
      case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/request-received':
        break
      case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/response-sent':
      // Resolve connection promise with the status ('accepted' or 'redirected') and pthid (InvitationId)
        connectionResolveMap.get(message.myDID)(['accepted', null])
        break
      case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/trust_ping/1.0/sent-response':
        break
      case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/out-of-band/1.0/relationship-reused':
      // Resolve connection promise with the status ('accepted' or 'redirected') and pthid (InvitationId)
        connectionResolveMap.get(inviteToDidMap.get(pthid))(['redirected', message.relationship])
        break
      case 'did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/answer-given':
      // Resolve question promise with the answer given
        questionResolveMap.get(threadId)(message.answer)
        break
      default:
      // Print out any response message from Verity which is not explicitly handled and exit
        console.log(`Unexpected message type ${message['@type']}`)
        console.log(`Message was:\n${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
        process.exit(1)
    }
  })

  // Serve static files (F/E) contained in the public folder
  app.use(express.static('public'))

  // Set templating engine to Pug
  // Pug is used to render accountPage.pug view differently depending on if the user is authenticated
  app.set('view engine', 'pug')
  app.set('views', './views')

  // Listen for messages from VAS
  server.listen(PORT, async () => {
    await initialize()
    console.log(`Listening on port ${PORT}`)
  })
}

main()
