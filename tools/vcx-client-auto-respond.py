#!/usr/bin/env python3.6

import asyncio
import base64
import json
import os
import sys
import random
import logging
import requests

from pathlib import Path
from aiohttp import web
from ctypes import cdll
from vcx.api.vcx_init import vcx_init
from vcx.api.connection import Connection
from vcx.api.issuer_credential import IssuerCredential
from vcx.api.proof import Proof
from vcx.api.disclosed_proof import DisclosedProof
from vcx.api.schema import Schema
from vcx.api.credential_def import CredentialDef
from vcx.api.credential import Credential
from vcx.api.wallet import Wallet
from vcx.state import State, ProofState
from time import sleep
from vcx.api.utils import vcx_agent_provision, vcx_messages_download,vcx_messages_update_status

WALLET_NAME = 'vcx-client-autorespond-wallet'
WALLET_PATH = f'{str(Path.home())}/wallet/{WALLET_NAME}'
WALLET_KEY = '12345'
AGENCY_URL = 'https://agency-team1.pdev.evernym.com'
VCX_CONFIG_PATH = f'{str(Path.cwd())}/vcx_client_config.json'
GENESIS_FILE = f'{str(Path.cwd())}/team1.txn'
data = {}
routes = web.RouteTableDef()

async def http_get(url):
    response = requests.get(url)
    if(response):
        return json.loads(response.content.decode('utf-8'))
    else:
        print(f'Request to {url} failed!')

async def init():
    if os.path.exists(WALLET_PATH):
        os.rmdir(WALLET_PATH)
    if os.path.exists(VCX_CONFIG_PATH):
        os.remove(VCX_CONFIG_PATH)

    agency_details = await http_get(f'{AGENCY_URL}/agency')

    provision_config = {
        'agency_url': AGENCY_URL,
        'agency_did': agency_details["DID"],
        'agency_verkey': agency_details["verKey"],
        'wallet_name': WALLET_NAME,
        'wallet_key': WALLET_KEY
    }

    config = await vcx_agent_provision(json.dumps(provision_config))
    config = json.loads(config)
    config['institution_name'] = 'none'
    config['institution_logo_url'] = 'https://i.postimg.cc/wx5JW5Xd/cookie.png' # Shouldn't need this
    config['genesis_path'] = GENESIS_FILE
    config['payment_method'] = 'null'

    with open(VCX_CONFIG_PATH, 'w') as outfile:
        json.dump(config, outfile)

    print('Provisioning complete. Config written to {}'.format(VCX_CONFIG_PATH))


async def init_vcx():
    lib = cdll.LoadLibrary('/usr/lib/libnullpay.so')
    lib.nullpay_init()
    await vcx_init(VCX_CONFIG_PATH)

async def new_connection(invite_details = None):
    await init_vcx()
    if(not invite_details):
        invite_details = input("Invite Details: ")
        invite_details = json.loads(invite_details)
    connection = await Connection.create_with_details('vcxapp', json.dumps(invite_details))
    await connection.connect('{"use_public_did": true}')
    await connection.update_state()
    current_state = await connection.get_state()

    global data
    data['connections'] = []
    data['handled_offers'] = []
    data['handled_requests'] = []

    data['connections'].append(await connection.serialize())
    data = {
        "connections": data['connections'],
        "handled_offers": data['handled_offers'],
        "handled_requests": data['handled_requests']
    }
    print(f'Connection successfully added')

async def handle_messages(my_connection, handled_offers, handled_requests):
    # Get Questions
    messages = await vcx_messages_download('MS-103', None, None);
    messages = json.loads(messages.decode('utf-8'))
    if(len(messages[0]['msgs']) > 0):
        print(messages)
    for msg_group in messages:
        for message in msg_group['msgs']:
            if (message['type'].lower() == 'question'):
                await handle_question(my_connection, message)
            await vcx_messages_update_status(json.dumps([{
                'pairwiseDID': msg_group['pairwiseDID'],
                'uids': [message['uid']]
            }]))

    # Get Cred Offers
    offers = await Credential.get_offers(my_connection)
    for offer in offers:
        handled = False
        for handled_offer in handled_offers:
            if offer[0]['msg_ref_id'] == handled_offer['msg_ref_id']:
                handled = True
                break
        if not handled:
            save_offer = offer[0].copy()
            print(" >>> handling offer", save_offer['msg_ref_id'])
            await handle_credential_offer(my_connection, offer)
            handled_offers.append(save_offer)

    # Get Proof Requests
    requests = await DisclosedProof.get_requests(my_connection)
    for request in requests:
        handled = False
        for handled_request in handled_requests:
            if request['msg_ref_id'] == handled_request['msg_ref_id']:
                handled = True
                break
        if not handled:
            save_request = request.copy()
            print(" >>> handling proof", save_request['msg_ref_id'])
            await handle_proof_request(my_connection, request)
            handled_requests.append(save_request)

async def handle_question(my_connection, message):
    question = json.loads(json.loads(message['decryptedPayload'])['@msg'])
    data = base64.b64encode(question['valid_responses'][0]['nonce'].encode())
    signature = await my_connection.sign_data(data)

    answer = {
        "@type": "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/committedanswer/1.0/answer",
        "response.@sig": {
            "signature": base64.b64encode(signature).decode('utf-8'),
            "sig_data": data.decode('utf-8'),
            "timestamp": "2018-12-13T17:29:34+0000"
        }
    }
    await my_connection.send_message(json.dumps(answer), "answer", "alice has answered your question")
    print("Answered Question")

async def handle_credential_offer(my_connection, offer):
    credential = await Credential.create('credential', offer)

    await credential.send_request(my_connection, 0)

    credential_data = await credential.serialize()

    while True:
        my_credential = await Credential.deserialize(credential_data)
        await my_credential.update_state()
        credential_state = await my_credential.get_state()
        if credential_state == State.Accepted:
            break
        else:
            credential_data = await my_credential.serialize()
            sleep(2)

    print("Accepted Credential Offer")


async def handle_proof_request(my_connection, request):
    proof = await DisclosedProof.create('proof', request)
    credentials = await proof.get_creds()

    self_attested = {}

    for attr in credentials['attrs']:
        if 0 < len(credentials['attrs'][attr]):
            credentials['attrs'][attr] = {
                'credential': credentials['attrs'][attr][0]
            }
        else:
            print("using self attested attributes")
            self_attested[attr] = 'my self-attested value'

    for attr in self_attested:
        del credentials['attrs'][attr]

    await proof.generate_proof(credentials, self_attested)

    # TODO figure out why this always segfaults
    await proof.send_proof(my_connection)

    proof_data = await proof.serialize()

    while True:
        my_proof = await DisclosedProof.deserialize(proof_data)
        await my_proof.update_state()
        proof_state = await my_proof.get_state()
        if proof_state == State.Accepted:
            break
        else:
            proof_data = await my_proof.serialize()
            sleep(2)

    print("Sent Proof")

async def autorespond():
    # await init_vcx()

    global data

    while True:
        for connection in data['connections']:
            connection = await Connection.deserialize(connection)
            await connection.update_state()
            state = await connection.get_state()
            await handle_messages(connection, data['handled_offers'], data['handled_requests'])

        sleep(3)

@routes.post('/connect')
async def connect_handler(request):
    await new_connection(await request.json())
    return web.Response(text="Success")

async def main(loop):
    if not os.path.exists(VCX_CONFIG_PATH):
        await init()
    if(len(sys.argv) == 2):
        invite_details = json.loads(sys.argv[1])
        await new_connection(invite_details)
        await autorespond()
    else:
        app = web.Application(loop=loop)
        app.add_routes(routes)
        await loop.create_server(app.make_handler(), '0.0.0.0', 4002)
        await loop.create_task(autorespond())


if __name__ == '__main__':
    loop = asyncio.get_event_loop()
    loop.run_until_complete(main(loop))
    loop.run_forever()
