#!/usr/bin/env python3

__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."

import json
import argparse
import asyncio
import time
import os
import sys
import requests
import logging
import json

from indy import did, wallet, crypto
from indy.error import ErrorCode, IndyError

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("AGENCY_URL")
    parser.add_argument("WALLET_KEY")
    parser.add_argument("--wallet-name", help="optional name for libindy wallet", default="wallet")
    # parser.add_argument("--wallet-type", help="optional type of libindy wallet")
    parser.add_argument("--verbose", action="store_true")
    return parser.parse_args()


def get_agency_info(agency_url):
    response = requests.get("{}/agency".format(agency_url))
    if (not response.ok):
        print("Unable to retrieve agency info from: {}/agency".format(agency_url))
        sys.exit(1)
    return response.json()


async def send_msg(url, my_wallet, message_json, agency_did, agency_verkey, my_verkey):
    agency_message = await crypto.pack_message(my_wallet, json.dumps(message_json), [agency_verkey], my_verkey)

    forward_msg = {
        "@type":"did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD",
        "@fwd":agency_did,
        "@msg":json.loads(agency_message.decode('utf-8'))
    }

    message = await crypto.pack_message(my_wallet, json.dumps(forward_msg), [agency_verkey], None)
    response = requests.post('{}/agency/msg'.format(url), data=message,headers={'Content-Type': 'application/octet-stream'})

    if(response.status_code != 200):
        print('Unable to POST message to agency: {}', response.content) # TODO: Add more useful error messages here.
        sys.exit(1)

    agent_message = await crypto.unpack_message(my_wallet, response.content)

    return json.loads(agent_message.decode('utf-8'))


async def register_agent(args):
    agency_info = get_agency_info(args.AGENCY_URL)

    ## Create a wallet
    wallet_config = json.dumps({"id": args.wallet_name})
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


    def buildMsgTypePrefix(familyName, msgName):
        return 'did:sov:123456789abcdefghi1234;spec/{}/0.6/{}'.format(familyName, msgName)

    ## Form messages
    create_agent = {
        "@type": buildMsgTypePrefix("agent-provisioning", "CREATE_AGENT"),
        "fromDID": my_did,
        "fromDIDVerKey": my_verkey
    }

    """
    create_agent_response = {
        "@type": "did:sov:123456789abcdefghi1234;spec/provision/1.0/create_agent_response",
        "withPairwiseDID": None,
        "withPairwiseDIDVerKey": None
    }
    """

    response = await send_msg(args.AGENCY_URL, my_wallet, create_agent, agency_info['DID'], agency_info['verKey'], my_verkey)

    # Use latest only in config.
    response = json.loads(response['message'])
    their_did = response['withPairwiseDID']
    their_verkey = response['withPairwiseDIDVerKey']

    ## Build sdk config

    final_config = {
        "walletName": args.wallet_name,
        "walletKey": args.WALLET_KEY,
        "verityUrl": "{}/agency/msg".format(args.AGENCY_URL),
        "verityPublicDID": agency_info['DID'],
        "verityPublicVerkey": agency_info['verKey'],
        "verityPairwiseDID": their_did,
        "verityPairwiseVerkey": their_verkey,
        "sdkPairwiseDID": my_did,
        "sdkPairwiseVerkey": my_verkey,
        "endpointUrl": "http://localhost:4000" # TODO: Should eventually be "<CHANGE ME>"
    }

    ## Print sdk config (admin will place in config file)
    print(json.dumps(final_config, indent=2, sort_keys=True))

    # For now, delete wallet
    await wallet.close_wallet(my_wallet)


async def main():
    args = parse_args()

    if args.verbose:
        logging.basicConfig(level=logging.INFO)
    else:
        logging.basicConfig(level=logging.ERROR)

    await register_agent(args)


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    time.sleep(.1)
