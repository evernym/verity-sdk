#!/usr/bin/env node

'use strict'
const sdk = require('./src/index')

main(process.argv[2])

async function main (command) {
  try {
    switch (command) {
      case 'provision':
        console.log(await provision())
        break
      case 'prov-update':
        await updateEndpoint(await provision())
        break
      case null:
        console.log('Please specify a command')
        break
      default:
        console.log(`Command "${command}" not found`)
        break
    }
  } catch (e) {
    console.error(e)
  }
}

async function provision () {
  let context = await sdk.Context.create(sdk.utils.miniId(), '12345', 'http://vas-team1.pdev.evernym.com/', 'http://localhost:4005')
  const provision = new sdk.protocols.Provision()
  context = await provision.provisionSdk(context)
  const contextString = JSON.stringify(context, null, 2)
  context.closeWallet()
  return contextString
}

async function updateEndpoint (config) {
  const context = await sdk.Context.createWithConfig(config)
  const updateEndpoint = new sdk.protocols.UpdateEndpoint()
  await updateEndpoint.update(context)
}
