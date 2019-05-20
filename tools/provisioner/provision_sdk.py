#!/usr/bin/env python3.6
# Provided by The Python Standard Library
import json
import argparse
import asyncio
import time
import os
import sys
import requests

from indy import did, wallet, crypto
from indy.error import ErrorCode, IndyError

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("AGENCY_URL")
    parser.add_argument("WALLET_KEY")
    parser.add_argument("--wallet-name", help="optional name for libindy wallet")
    # parser.add_argument("--wallet-type", help="optional type of libindy wallet")
    parser.add_argument("--verbose", action="store_true") # FIXME: Doesn't support new indy logger
    return parser.parse_args()


def get_agency_info(agency_url):
    response = requests.get("{}/agency".format(agency_url))
    if (not response.ok):
        print("Unable to retrieve agency info from: {}/agency".format(agency_url))
        sys.exit(1)
    return response.json()


async def send_msg(url, my_wallet, message_json, agency_verkey):
    agency_message = await crypto.pack_message(my_wallet, json.dumps(message_json), [agency_verkey], None)
    response = requests.post('{}/agency'.format(url), data=agency_message,headers={'Content-Type': 'application/octet-stream'})
    if(response.status_code != 200):
        print('Unable to POST message to agency') # TODO: Add more useful error messages here.
        sys.exit(1)

    agent_message = await crypto.unpack_message(my_wallet, response.content)

    return json.loads(agent_message)


async def register_agent(args):
    agency_info = get_agency_info(args.AGENCY_URL)

    ## Create a wallet
    wallet_config = json.dumps({"id": args.wallet_name or 'wallet' })
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
    my_did, my_verkey = await did.create_and_store_my_did(my_wallet, json.dumps({}))


    ## Form messages
    connect_msg = {
        "@type": 'vs.service/provision/1.0/connect',
        "fromDID": my_did,
        "fromDIDVerKey": my_verkey
    }

    """
    connect_response = {
        "@type": 'vs.service/provision/1.0/connect_response',
        "withPairwiseDID": None,
        "withPairwiseDIDVerKey": None
    }
    """

    # anoncrypt_for_agency(anoncrypt_for_agency(msg))
    response = await send_msg(args.AGENCY_URL, my_wallet, connect_msg, agency_info['verKey'])

    response = json.loads(response['message'])
    their_did = response['withPairwiseDID']
    their_verkey = response['withPairwiseDIDVerKey']

    signup_msg = {
        "@type": 'vs.service/provision/1.0/signup',
    }

    """
    signup_response = {
        "@type": 'vs.service/provision/1.0/signup_response',
    }
    """

    # anoncrypt_for_agency()
    response = await send_msg(args.AGENCY_URL, my_wallet, signup_msg, agency_info['verKey'])

    create_agent_msg = {
        "@type": "vs.service/provision/1.0/create_agent",
    }

    """
    create_agent_response = {
        "@type": "vs.service/provision/1.0/create_agent_response",
        "withPairwiseDID": None,
        "withPairwiseDIDVerKey": None
    }
    """

    response = await send_msg(args.AGENCY_URL, my_wallet, create_agent_msg, agency_info['verKey'])

    # Use latest only in config. 
    response = json.loads(response['message'])
    their_did = response['withPairwiseDID']
    their_verkey = response['withPairwiseDIDVerKey']

    ## Build sdk config

    final_config = {
        "walletName": args.wallet_name,
        "walletKey": args.WALLET_KEY,
        "agencyUrl": args.AGENCY_URL,
        "agencyPublicVerkey": agency_info['verKey'],
        "agencyPairwiseVerkey": their_verkey,
        "sdkPairwiseVerkey": my_verkey,
        "webhookUrl": "<CHANGE ME>"
    }

    ## Print sdk config (admin will place in config file)
    print(json.dumps(final_config, indent=2, sort_keys=True))

    # For now, delete wallet
    await wallet.close_wallet(my_wallet)


async def main():
    args = parse_args()

    if args.verbose:
        os.environ["RUST_LOG"] = "info"
    else:
        os.environ["RUST_LOG"] = "error"

    await register_agent(args)


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    time.sleep(.1)
