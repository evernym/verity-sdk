#!/usr/bin/env node

'use strict'

main(process.argv[2])

async function main (command) {
  try {
    switch (command) {
      case 'provision':
        await provision()
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
  const sdk = require('./src/index')
  const context = await sdk.Context.create(sdk.utils.miniId(), '12345', 'http://vas-team1.pdev.evernym.com/')
  const provision = new sdk.protocols.Provision()
  await provision.provisionSdk(context)
}
