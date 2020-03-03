__copyright__ = 'COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.'

import asyncio
import json
import logging
import traceback
from asyncio.base_events import Server

import pyqrcode
from aiohttp import web
from aiohttp.web_routedef import RouteTableDef

from example.helper import *
from src.handlers import Handlers
from src.protocols.Connecting import Connecting
from src.protocols.IssueCredential import IssueCredential
from src.protocols.IssuerSetup import IssuerSetup
from src.protocols.PresentProof import PresentProof
from src.protocols.Provision import Provision
from src.protocols.UpdateEndpoint import UpdateEndpoint
from src.protocols.WriteCredentialDefinition import WriteCredentialDefinition
from src.protocols.WriteSchema import WriteSchema
from src.utils import truncate_invite_details, uuid
from src.utils.Context import Context
from src.utils.Did import Did, create_new_did

context: Context
issuer_did: str = ''
issuer_verkey: str = ''

server: Server
port: int = 4000
handlers: Handlers = Handlers()
handlers.set_default_handler(default_handler)

routes: RouteTableDef = web.RouteTableDef()


async def example(loop):
    logging.info('Starting setup')
    await setup(loop)

    for_did = await create_connection(loop)

    # await ask_question(for_did)

    schema_id = await write_ledger_schema(loop)
    cred_def_id = await write_ledger_cred_def(loop, schema_id)

    await issue_credential(loop, for_did, cred_def_id)

    await request_proof(loop, for_did)


async def create_connection(loop):
    global context
    global handlers

    # Connecting protocol has to steps
    # 1. Start the protocol and receive the invite
    # 2. Wait for the other participant to accept the invite

    # Step 1

    # Constructor for the Connecting API
    connecting: Connecting = Connecting(include_public_did=True)

    first_step = loop.create_future()

    spinner = make_spinner('Waiting to start connection')  # Console spinner

    # handler for the response to the request to start the Connecting protocol.
    async def invite_detail_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == Connecting.INVITE_DETAIL:
            invite_detail = message['inviteDetail']
            did = invite_detail['senderDetail']['DID']
            truncated_invite_detail = truncate_invite_details(invite_detail)
            # print(truncated_invite_detail)

            # write QRCode to disk
            qr = pyqrcode.create(json.dumps(truncated_invite_detail))
            qr.png('qrcode.png')

            print()
            print('QR code at: qrcode.png')

            first_step.set_result(did)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, invite_detail_handler)

    spinner.start()

    # starts the connecting protocol
    await connecting.connect(context)
    for_did = await first_step  # wait for response from verity application

    # Step 2

    second_step = loop.create_future()

    spinner = make_spinner('Waiting to start connection')  # Console spinner

    # handler for the accept message sent when connection is accepted
    async def connection_accepted_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == Connecting.CONN_REQ_ACCEPTED:
            second_step.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    spinner.start()
    # note this overrides the handler for this message family! This is for demonstration purposes only.
    handlers.add_handler(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, connection_accepted_handler)

    await second_step  # wait for acceptance from connect.me user
    return for_did  # return owning DID for the connection


async def write_ledger_schema(loop) -> str:

    # input parameters for schema
    schema_name = 'Diploma'
    schema_version = get_random_version()
    schema_attrs = ['name', 'degree']

    # constructor for the Write Schema protocol
    schema = WriteSchema(schema_name, schema_version, schema_attrs)

    first_step = loop.create_future()

    spinner = make_spinner('Waiting to write schema to ledger')  # Console spinner

    # handler for message received when schema is written
    async def schema_written_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == WriteSchema.STATUS:
            first_step.set_result(message['schemaId'])
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION, schema_written_handler)

    spinner.start()

    # request schema be written to ledger
    await schema.write(context)
    schema_id = await first_step  # wait for operation to be complete
    return schema_id  # returns ledger schema identifier


async def write_ledger_cred_def(loop, schema_id: str) -> str:

    # input parameters for cred definition
    cred_def_name = 'Trinity College Diplomas'
    cred_def_tag = 'latest'

    # constructor for the Write Credential Definition protocol
    cred_def = WriteCredentialDefinition(cred_def_name, schema_id, cred_def_tag)

    first_step = loop.create_future()

    spinner = make_spinner('Waiting to write cred def to ledger')  # Console spinner

    # handler for message received when schema is written
    async def cred_def_written_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == WriteCredentialDefinition.STATUS:
            first_step.set_result(message['credDefId'])
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(
        WriteCredentialDefinition.MSG_FAMILY,
        WriteCredentialDefinition.MSG_FAMILY_VERSION,
        cred_def_written_handler
    )

    spinner.start()

    # request the cred def be writen to ledger
    await cred_def.write(context)
    cred_def_id = await first_step  # wait for operation to be complete
    return cred_def_id # returns ledger cred def identifier


async def issue_credential(loop, for_did, cred_def_id):

    # input parameters for issue credential
    credential_name = 'Degree'
    credential_data = {
        'name': 'Joe Smith',
        'degree': 'Bachelors'
    }

    # constructor for the Issue Credential protocol
    issue = IssueCredential(for_did, None, credential_name, cred_def_id, credential_data)

    first_step = loop.create_future()
    spinner = make_spinner('Wait for Connect.me to accept the Credential Offer')  # Console spinner

    # handler for 'ask_accept` message when the offer for credential is accepted
    async def cred_offer_accepted_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == IssueCredential.ASK_ACCEPT:
            first_step.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION, cred_offer_accepted_handler)

    spinner.start()
    # request that credential is offered
    await issue.offer_credential(context)
    await first_step  # wait for connect.me user to accept offer

    # request that credential be issued
    await issue.issue_credential(context)
    await asyncio.sleep(3)  # Wait a few seconds for the credential to arrive before sending the proof


async def request_proof(loop, for_did):
    global issuer_did

    # input parameters for request proof
    proof_name = 'Proof of Degree'
    proof_attrs = [
        {
            'name': 'name',
            'restrictions': [{'issuer_did': issuer_did}]
        },
        {
            'name': 'degree',
            'restrictions': [{'issuer_did': issuer_did}]
        }
    ]

    # constructor for the Present Proof protocol
    proof = PresentProof(for_did, None, proof_name, proof_attrs)

    spinner = make_spinner('Waiting for proof presentation from Connect.me')  # Console spinner
    first_step = loop.create_future()

    # handler for the result of the proof presentation
    async def proof_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == PresentProof.PROOF_RESULT:
            first_step.set_result(None)  # proof data contained inside `message`
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(PresentProof.MSG_FAMILY, PresentProof.MSG_FAMILY_VERSION, proof_handler)

    spinner.start()

    # request proof
    await proof.request(context)
    await first_step  # wait for connect.me user to present the requested proof


async def setup(loop):
    global context
    global issuer_did

    # look for context on disk
    config = load_context('verity-context.json')
    if config:
        context = await Context.create_with_config(config)
    else:
        context = await provision_agent()

    await update_webhook_endpoint()

    await issuer_identifier(loop)

    if not issuer_did:
        await setup_issuer(loop)

    print_object(context.to_json(indent=2), '>>>', 'Context Used:')

    with open('verity-context.json', 'w') as f:
        f.write(context.to_json())


async def provision_agent() -> str:
    global context
    wallet_name = 'examplewallet1'  # for libindy wallet
    wallet_key = 'examplewallet1'

    verity_url = console_input(f'Verity Application Endpoint').strip()

    # create initial Context
    context = await Context.create(wallet_name, wallet_key, verity_url, '')

    # create and store sdk public/private key pair (identified by DID)
    sdk_pairwise_did_verkey: Did = await create_new_did(context.wallet_handle)

    # add sdk did/verkey to Context
    context.sdk_pairwise_did = sdk_pairwise_did_verkey.did
    context.sdk_pairwise_verkey = sdk_pairwise_did_verkey.verkey

    # ask that an agent by provision (setup) and associated with created key pair
    context = await Provision().provision_sdk(context)
    return context


async def update_webhook_endpoint():
    global context, port
    webhook_from_ctx: str = context.endpoint_url

    if not webhook_from_ctx:
        # Default to localhost on the default port
        webhook_from_ctx = f'http://localhost:{port}'

    webhook: str = console_input(f'Ngrok endpoint [{webhook_from_ctx}]')

    if not webhook:
        webhook = webhook_from_ctx

    print(f'Using Webhook: {webhook}')
    print()
    context.endpoint_url = webhook

    # request that verity application use specified webhook endpoint
    await UpdateEndpoint(context).update()


async def issuer_identifier(loop):

    # constructor for the Issuer Setup protocol
    issuer_setup = IssuerSetup()

    first_step = loop.create_future()

    spinner = make_spinner('Waiting for current issuer DID')  # Console spinner

    # handler for current issuer identifier message
    async def current_identifier(msg_name, message):
        global issuer_did
        global issuer_verkey

        spinner.stop_and_persist('Done')

        if msg_name == IssuerSetup.PUBLIC_IDENTIFIER:
            issuer_did = message['did']
            issuer_verkey = message['verKey']
            first_step.set_result(None)
        elif msg_name == IssuerSetup.PROBLEM_REPORT:
            # Do nothing. Just means we need to write the keys to the ledger.
            first_step.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(IssuerSetup.MSG_FAMILY, IssuerSetup.MSG_FAMILY_VERSION, current_identifier)

    spinner.start()

    # query the current identifier
    await issuer_setup.current_public_identifier(context)
    await first_step  # wait for response from verity application


async def setup_issuer(loop):

    # constructor for the Issuer Setup protocol
    issuer_setup = IssuerSetup()

    first_step = loop.create_future()

    spinner = make_spinner('Waiting for setup to complete') # Console spinner

    # handler for created issuer identifier message
    async def public_identifier_handler(msg_name, message):
        global issuer_did
        global issuer_verkey

        spinner.stop_and_persist('Done')

        if msg_name == IssuerSetup.PUBLIC_IDENTIFIER_CREATED:
            issuer_did = message['identifier']['did']
            issuer_verkey = message['identifier']['verKey']
            print('The issuer DID and Verkey must be on the ledger.')
            print(f'Please add DID ({issuer_did}) and Verkey ({issuer_verkey}) to ledger.')
            console_input('Press ENTER when DID is on ledger')
            first_step.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}')

    # adds handler to the set of handlers
    handlers.add_handler(IssuerSetup.MSG_FAMILY, IssuerSetup.MSG_FAMILY_VERSION, public_identifier_handler)

    spinner.start()

    # request that issuer identifier be created
    await issuer_setup.create(context)

    await first_step  # wait for request to complete


@routes.post('/')
async def endpoint_handler(request):
    try:
        await handlers.handle_message(context, await request.read())
        return web.Response(text='Success')
    except Exception as e:
        traceback.print_exc()
        return web.Response(text=str(e))


async def main(loop):
    global port
    global server

    app = web.Application(loop=loop)
    app.add_routes(routes)

    # noinspection PyDeprecation
    server = await loop.create_server(app.make_handler(), '0.0.0.0', port)

    print('Listening on port {}'.format(port))
    await loop.create_task(example(loop))


if __name__ == '__main__':
    mainloop = asyncio.get_event_loop()
    mainloop.run_until_complete(main(mainloop))
