#!/usr/bin/env python3

import argparse
import asyncio
import json
import sys
import time

from indy import did, non_secrets, wallet
from indy.error import ErrorCode, IndyError

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("WALLET_NAME")
    parser.add_argument("WALLET_KEY")
    return parser.parse_args()

async def main():
    args = parse_args()

    agency_url = "http://agency.url"
    wallet_config = json.dumps({"id": args.WALLET_NAME})
    wallet_credentials = json.dumps({"key": args.WALLET_KEY})

    try:
        await wallet.create_wallet(wallet_config, wallet_credentials)
    except IndyError as ex:
        if ex.error_code == ErrorCode.WalletAlreadyExistsError:
            print("A wallet named {} already exists").format(args.wallet_name)
            sys.exit(1)
        else:
            print("An error occured creating the wallet")
            sys.exit(1)
    my_wallet = await wallet.open_wallet(wallet_config, wallet_credentials)

    my_pairwise_did, my_pairwise_verkey = await did.create_and_store_my_did(my_wallet, "{}")
    their_pairwise_did, their_pairwise_verkey = await did.create_and_store_my_did(my_wallet, "{}")
    their_public_did, their_public_verkey = await did.create_and_store_my_did(my_wallet, "{}")

    await non_secrets.add_wallet_record(my_wallet, "sdk_details", "agency_url", agency_url, None)
    await non_secrets.add_wallet_record(my_wallet, "sdk_details", "agency_did", their_public_did, None)
    await non_secrets.add_wallet_record(my_wallet, "sdk_details", "agency_pairwise_did", their_pairwise_did, None)
    await non_secrets.add_wallet_record(my_wallet, "sdk_details", "my_pairwise_did", my_pairwise_did, None)

    await wallet.close_wallet(my_wallet)

    print("agency_verkey: {}".format(their_public_verkey))
    print("agency_pairwise_verkey: {}".format(their_pairwise_verkey))
    print("my_pairwise_verkey: {}".format(my_pairwise_verkey))

    print("Wallet successfully initialized!")



if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    time.sleep(.1)
