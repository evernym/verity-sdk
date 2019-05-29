#!/usr/bin/env python3.6
import json
import argparse
import asyncio
import time
import os
import sys
import random
import requests
import logging

from ctypes import cdll
from uuid import uuid4

from vcx.api.connection import Connection
from vcx.api.credential_def import CredentialDef
from vcx.api.issuer_credential import IssuerCredential
from vcx.api.proof import Proof
from vcx.api.schema import Schema
from vcx.api.utils import vcx_agent_provision
from vcx.api.vcx_init import vcx_init
from vcx.state import State, ProofState

def parse_args():
    parser = argparse.ArgumentParser()
    parser.add_argument("SCHEMA_NAME")
    parser.add_argument("SCHEMA_VERSION")
    parser.add_argument("SCHEMA_ATTRS", nargs='*', help="Schema attributes. ")
    parser.add_argument("-c", "--config", help="path to vcx_config.json", default="./vcx_config.json")
    parser.add_argument("-v", "--verbose", action="store_true")
    args = parser.parse_args()

    if(not os.path.exists(args.config)):
        parser.error(f"The file \"{args.config}\" does not exist. Use -c or --config to specify the path to vcx_config.json")

    return args


def uuid():
    return str(uuid4())


async def main():
    args = parse_args()

    if(args.verbose):
        logging.basicConfig(level=logging.DEBUG)

    # Initialize libindy
    lib = cdll.LoadLibrary('/usr/lib/libnullpay.so')
    lib.nullpay_init()
    await vcx_init(args.config)

    # Write Schema to Ledger
    schema_source_id = uuid()
    schema = await Schema.create(schema_source_id, args.SCHEMA_NAME, args.SCHEMA_VERSION, args.SCHEMA_ATTRS, payment_handle=0)
    schema_id = await schema.get_schema_id()

    final_config = {
        "schema": {
            "id": schema_id,
            "source_id": schema_source_id,
            "name": args.SCHEMA_NAME,
            "version": args.SCHEMA_VERSION,
            "attrs": args.SCHEMA_ATTRS
        }
    }

    print(json.dumps(final_config, indent=2))


if __name__ == "__main__":
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main())
    time.sleep(.1)
