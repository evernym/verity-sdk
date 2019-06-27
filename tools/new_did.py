#!/usr/bin/env python3

__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."

import asyncio
import base64
import json
import os
import sys

from indy import did, wallet

async def main():

    wallet_config = json.dumps({'id': 'new-did-temp-wallet'})
    wallet_credentials = json.dumps({'key': base64.urlsafe_b64encode(os.urandom(32)).decode('utf-8')})
    await wallet.create_wallet(wallet_config, wallet_credentials)
    wallet_handle = await wallet.open_wallet(wallet_config, wallet_credentials)

    try:
        if len(sys.argv) > 1:
            seed = sys.argv[1]
        else:
            seed = base64.urlsafe_b64encode(os.urandom(40)).decode('utf-8').replace('-', '').replace('_', '').replace('=', '')[:32]
        did_info = json.dumps({'seed': seed})
        my_did, my_verkey = await did.create_and_store_my_did(wallet_handle, did_info)

        print('DID (public): {}'.format(my_did))
        print('VerKey (public): {}'.format(my_verkey))
        print('Seed (private): {}'.format(seed))
        print()
        print('Don\'t share your seed with anyone! Keep it safe.')
        print('Send an email to support@sovrin.org and ask to be added to the StagingNet as a Trust Anchor.')
        print('Include your DID and VerKey in the email.')

    finally:
        await wallet.close_wallet(wallet_handle)
        await wallet.delete_wallet(wallet_config, wallet_credentials)

if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
