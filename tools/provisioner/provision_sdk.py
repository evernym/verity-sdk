#!/usr/bin/env python3.6
# Provided by The Python Standard Library
import json
import argparse
import asyncio
import time
import os
import urllib.request
import sys
import requests
from ctypes import *

from indy import did, wallet, crypto
from indy.error import ErrorCode, IndyError

TYPE_PREFIX = 'did:sov:123456789abcdefghi1234;spec/onboarding/1.0/'

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("AGENCY_URL")
    parser.add_argument("WALLET_KEY")
    parser.add_argument("--wallet-name", help="optional name for libindy wallet")
    parser.add_argument("--wallet-type", help="optional type of libindy wallet")
    parser.add_argument("--agent-seed", help="optional seed used to create enterprise->agent DID/VK")
    parser.add_argument("--enterprise-seed", help="optional seed used to create enterprise DID/VK")
    parser.add_argument("--verbose", action="store_true")
    return parser.parse_args()

def get_agency_info(agency_url):
    agency_info = {}
    agency_resp = ''
    #Get agency's did and verkey:
    try:
        agency_req=urllib.request.urlopen('{}/agency'.format(agency_url))
    except:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        sys.stderr.write("Failed looking up agency did/verkey: '{}': {}\n".format(exc_type.__name__,exc_value))
        print(json.dumps({
            'provisioned': False,
            'provisioned_status': "Failed: Could not retrieve agency info from: {}/agency: '{}': {}".format(agency_url,exc_type.__name__,exc_value)
        },indent=2))
        sys.exit(1)
    agency_resp = agency_req.read()
    try:
        agency_info = json.loads(agency_resp.decode())
    except:
        exc_type, exc_value, exc_traceback = sys.exc_info()
        sys.stderr.write("Failed parsing response from agency endpoint: {}/agency: '{}': {}\n".format(agency_url,exc_type.__name__,exc_value))
        sys.stderr.write("RESPONSE: {}".format(agency_resp))
        print(json.dumps({
            'provisioned': False,
            'provisioned_status': "Failed: Could not parse response from agency endpoint from: {}/agency: '{}': {}".format(agency_url,exc_type.__name__,exc_value)
        },indent=2))
        sys.exit(1)
    return agency_info


# pack once with provided key, then anoncrypt to agency
async def send_msg(url, my_wallet, message_json, agency_verkey, recipient_verkey, sender_verkey):
    agent_message = await crypto.pack_message(my_wallet, json.dumps(message_json), [recipient_verkey], sender_verkey)
    agency_message = await crypto.pack_message(my_wallet, agent_message, agency_verkey)
    response = requests.post(url, data=agency_message)
    if(response.status_code != 200):
        print('Unable to POST message to agency') # TODO: Add more useful error messages here.
        sys.exit(1)

    agent_message = await crypto.unpack_message(my_wallet, response.content)

    return agent_message


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
        "@type": "{}CONNECT".format(TYPE_PREFIX),
        "fromDID": my_did,
        "fromDIDVerKey": my_verkey
    }

    """
    connect_response = {
        "@type": "{}CONNECTED".format(TYPE_PREFIX),
        "withPairwiseDID": None,
        "withPairwiseDIDVerKey": None
    }
    """

    # anoncrypt_for_agency(anoncrypt_for_agency(msg))
    response = send_msg(args.AGENCY_URL, my_wallet, connect_msg, agency_info['verKey'], agency_info['verKey'])

    their_did = response.withPairwiseDID
    their_verkey = response.withPairwiseDIDVerKey

    signup_msg = {
        "@type": "{}SIGNUP".format(TYPE_PREFIX),
    }

    """
    signup_response = {
        "@type": "{}SIGNED_UP".format(TYPE_PREFIX),
    }
    """

    # anoncrypt_for_agency()
    response = send_msg(args.AGENCY_URL, my_wallet, signup_msg, agency_info['verKey'], their_verkey, my_verkey)

    create_agent_msg = {
        "@type": "{}CREATE_AGENT".format(TYPE_PREFIX),
    }

    """
    create_agent_response = {
        "@type": "{}AGENT_CREATED".format(TYPE_PREFIX),
        "withPairwiseDID": None,
        "withPairwiseDIDVerKey": None
    }
    """

    response = send_msg(args.AGENCY_URL, my_wallet, create_agent_msg, agency_info['verKey'], their_verkey, my_verkey)

    # Use latest only in config. 
    their_did = response.withPairwiseDID
    their_verkey = response.withPairwiseDIDVerKey

    ## Build sdk config

    final_config = {
        "wallet_name": args.wallet_name,
        "wallet_key": args.WALLET_KEY,
        "agency_endpoint": args.AGENCY_URL,
        "agency_did": agency_info['DID'],
        "agency_verkey": agency_info['verKey'],
        "sdk_to_remote_did": my_did,
        "sdk_to_remote_verkey": my_verkey,
        "remote_to_sdk_did": their_did,
        "remote_to_sdk_verkey": their_verkey
    }

    ## Print sdk config (admin will place in config file)
    print(json.dumps(final_config, indent=2, sort_keys=True))

    # For now, delete wallet
    await wallet.close_wallet(my_wallet)
    await wallet.delete_wallet(wallet_config, wallet_credentials)



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
