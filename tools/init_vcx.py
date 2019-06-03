#!/usr/bin/env python3
import argparse, asyncio, json, logging, os, requests, sys, time
from ctypes import cdll
from vcx.api.vcx_init import vcx_init
from vcx.api.utils import vcx_agent_provision

VCX_CONFIG_PATH = './vcx_config.json'

async def init(provision_config):
    lib = cdll.LoadLibrary('/usr/lib/libnullpay.so')
    lib.nullpay_init()
    if(os.path.exists(VCX_CONFIG_PATH)):
        print("Found {}".format(VCX_CONFIG_PATH))
    else:
        print("{} not found, provisioning agent with agency".format(VCX_CONFIG_PATH))
        agency_details = requests.get("{}/agency".format(provision_config["agency_url"])).json()
        provision_config["agency_did"] = agency_details["DID"]
        provision_config["agency_verkey"] = agency_details["verKey"]
        config = await vcx_agent_provision(json.dumps(provision_config))
        config = json.loads(config)
        config['institution_name'] = provision_config["institution_name"]
        config['institution_logo_url'] = provision_config["institution_logo_url"]
        config['genesis_path'] = provision_config["genesis_path"]
        config['enterprise_seed'] = provision_config["enterprise_seed"]
        config['payment_method'] = 'null'
        with open(VCX_CONFIG_PATH, 'w') as outfile:
            json.dump(config, outfile)

    print("Initializing libvcx...", end='')
    await vcx_init(VCX_CONFIG_PATH)
    print("Initialized")

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("--agency-url")
    parser.add_argument("--wallet-name")
    parser.add_argument("--wallet-key")
    parser.add_argument("--institution-name")
    parser.add_argument("--institution-logo-url")
    parser.add_argument("--genesis-path")
    parser.add_argument("--enterprise-seed")
    parser.add_argument("-v", "--verbose", action="store_true")
    return parser.parse_args()

async def main():
    args = parse_args()

    if(args.verbose):
        logging.basicConfig(level=logging.DEBUG)
    
    # Initialize libvcx
    provision_config = {
        "agency_url": args.agency_url or "https://eas-team1.pdev.evernym.com",
        "wallet_name": args.wallet_name or "verity-sdk-wallet",
        "wallet_key": args.wallet_key or "12345",
        "institution_name": args.institution_name or "Faber",
        "institution_logo_url": args.institution_logo_url or "https://i.postimg.cc/J0FWGN7r/Screen-Dev.png",
        "genesis_path": args.genesis_path or "team1.txn",
        "enterprise_seed": args.enterprise_seed or "000000000000000000000000Trustee1"
    }
    await init(provision_config)

if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    time.sleep(.1)