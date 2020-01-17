'use strict'
const sdk = require('./src/index')

main()

async function main () {
  try {
    ////////////////////////////////

    let context = await sdk.Context.create(sdk.utils.miniId(), '12345', 'http://vas-team1.pdev.evernym.com/', 'http://localhost:4005')
    const provision = new sdk.protocols.Provision()
    context = await provision.provisionSdk(context)
    context.closeWallet()
    return JSON.stringify(context.getConfig(), null, 2)

    ////////////////////////////////
  } catch (e) {
    console.error(e)
  }
}
