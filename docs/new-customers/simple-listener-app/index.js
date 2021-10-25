const bodyParser = require('body-parser')
const express = require('express')

const ANSII_GREEN = '\u001b[32m'
const ANSII_RESET = '\x1b[0m'
const PORT = 4000

const app = express()

app.use(bodyParser.json())

app.post('/', async (req, res) => {
  const message = req.body
  console.log('Got message on the webhook')
  console.log(`${ANSII_GREEN}${JSON.stringify(message, null, 4)}${ANSII_RESET}`)
  res.status(202).send('Accepted')
})

app.listen(PORT, () => {
  console.log(`Webhook listening on port ${PORT}`)
})
