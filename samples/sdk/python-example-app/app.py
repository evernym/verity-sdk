__copyright__ = 'COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC.'

import logging
import os
import pyqrcode
import requests
import traceback
import asyncio
from asyncio.base_events import Server
from aiohttp import web
from aiohttp.web_routedef import RouteTableDef
from verity_sdk.handlers import Handlers
from verity_sdk.protocols.v0_6.IssuerSetup import IssuerSetup
from verity_sdk.protocols.v0_6.UpdateConfigs import UpdateConfigs
from verity_sdk.protocols.v0_6.UpdateEndpoint import UpdateEndpoint
from verity_sdk.protocols.v0_6.WriteCredentialDefinition import WriteCredentialDefinition
from verity_sdk.protocols.v0_6.WriteSchema import WriteSchema
from verity_sdk.protocols.v0_7.Provision import Provision
from verity_sdk.protocols.v1_0.Connecting import Connecting
from verity_sdk.protocols.v1_0.IssueCredential import IssueCredential
from verity_sdk.protocols.v1_0.PresentProof import PresentProof
from verity_sdk.protocols.v1_0.Relationship import Relationship
from verity_sdk.protocols.v1_0.CommittedAnswer import CommittedAnswer
from verity_sdk.utils.Context import Context
from vdrtools.wallet import delete_wallet
from vdrtools.error import WalletNotFoundError

from helper import *

INSTITUTION_NAME: str = 'Faber College'
LOGO_URL: str = 'https://freeiconshop.com/wp-content/uploads/edd/bank-flat.png'
CONFIG_PATH: str = 'verity-context.json'
WALLET_NAME: str = 'examplewallet1'
WALLET_KEY: str = 'examplewallet1'

context: Context
issuer_did: str = ''
issuer_verkey: str = ''

server: Server
port: int = 4000
handlers: Handlers = Handlers()
handlers.set_default_handler(default_handler)
handlers.add_handler('trust_ping', '1.0', noop)

routes: RouteTableDef = web.RouteTableDef()


async def example(loop):
    logging.info('Starting setup')
    await setup(loop)

    rel_did = await create_relationship(loop)
    await create_connection(loop)

    await ask_question(loop, rel_did)

    schema_id = await write_ledger_schema(loop)
    cred_def_id = await write_ledger_cred_def(loop, schema_id)

    await issue_credential(loop, rel_did, cred_def_id)

    await request_proof(loop, rel_did)


async def create_relationship(loop) -> str:
    global context
    global handlers

    # Relationship protocol has two steps
    # 1. create relationship key
    # 2. create invitation

    # Constructor for the Relationship API
    relationship: Relationship = Relationship()

    rel_did = loop.create_future()
    thread_id = loop.create_future()

    spinner = make_spinner('Waiting to create relationship')  # Console spinner

    # handler for the response to the request to start the Connecting protocol.
    async def created_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == Relationship.CREATED:
            thread_id.set_result(message['~thread']['thid'])
            rel_did.set_result(message['did'])
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(Relationship.MSG_FAMILY, Relationship.MSG_FAMILY_VERSION, created_handler)

    spinner.start()

    # starts the relationship protocol
    await relationship.create(context)
    thread_id = await thread_id
    rel_did = await rel_did

    # Step 2
    invitation = loop.create_future()

    spinner = make_spinner('Waiting to create invitation')  # Console spinner

    # handler for the accept message sent when invitation is created
    async def invitation_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == Relationship.INVITATION:
            invite_url = message['inviteURL']
            # write QRCode to disk
            qr = pyqrcode.create(invite_url)
            qr.png('qrcode.png')

            if os.environ.get('HTTP_SERVER_URL'):
                print('Open the following URL in your browser and scan presented QR code')
                print(f'{ANSII_GREEN}{os.environ.get("HTTP_SERVER_URL")}/python-example-app/qrcode.html{ANSII_RESET}')
            else:
                print('QR code generated at: qrcode.png')
                print('Open this file and scan QR code to establish a connection')
            invitation.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    spinner.start()
    # note this overrides the handler for this message family! This is for demonstration purposes only.
    handlers.add_handler(Relationship.MSG_FAMILY, Relationship.MSG_FAMILY_VERSION, invitation_handler)

    relationship: Relationship = Relationship(rel_did, thread_id)
    await relationship.out_of_band_invitation(context)
    await invitation
    return rel_did  # return owning DID for the connection


async def create_connection(loop):
    global context
    global handlers

    # Connecting protocol is started from the Holder's side (ConnectMe)
    # by scanning the QR code containing connection invitation
    # Connection is established when the Holder accepts the connection on the device
    # i.e. when the RESPONSE_SENT control message is received

    connection = loop.create_future()

    spinner = make_spinner('Waiting to respond to connection')  # Console spinner

    # handler for messages in Connecting protocol
    async def connection_handler(msg_name, message):
        if msg_name == Connecting.REQUEST_RECEIVED:
            print()
            print_message(msg_name, message)
        elif msg_name == Connecting.RESPONSE_SENT:
            spinner.stop_and_persist('Done')
            print_message(msg_name, message)
            connection.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # handler for relationship-reused message which is sent by the ConnectMe app if it is already connected with this example app agent
    async def releationship_reused_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == 'relationship-reused':
            print('The mobile wallet app signalled that it already has the connection with this example app agent')
            print('This example app does not support relationship-reuse since it does not store the data about previous relationships')
            print('Please delete existing connection in your mobile wallet and re-run this application')
            print('To learn how relationship-reuse can be used check out "ssi-auth" or "out-of-band" sample apps')
            sys.exit(1)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, connection_handler)
    handlers.add_handler('out-of-band', '1.0', releationship_reused_handler)

    spinner.start()

    # waits for request
    await connection  # wait for response from verity application

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
        if msg_name == 'needs-endorsement':
            print('Please manually endorse this schema json. If you do not have an endorser DID on the ledger send it to Evernym to be endorsed: ')
            print(message['schemaJson'])
            console_input('Press enter when the schema has been endorsed', None)
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
        if msg_name == 'needs-endorsement':
            print('Please manually endorse this cred def json. If you do not have an endorser DID on the ledger send it to Evernym to be endorsed: ')
            print(message['credDefJson'])
            first_step.set_result(message['credDefId'])
            console_input('Press enter when the cred def has been endorsed', None)
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
    return cred_def_id  # returns ledger cred def identifier


async def ask_question(loop, for_did):
    question_text = 'Hi Alice, how are you today?'
    question_detail = 'Checking up on you today.'
    valid_responses = ['Great!', 'Not so good.']

    question = CommittedAnswer(for_did, None, question_text, question_detail, valid_responses, True)
    first_step = loop.create_future()

    spinner = make_spinner('Waiting for Connect.Me to answer the question')  # Console spinner

    async def receive_answer(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == CommittedAnswer.ANSWER_GIVEN:
            first_step.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    handlers.add_handler(CommittedAnswer.MSG_FAMILY, CommittedAnswer.MSG_FAMILY_VERSION, receive_answer)

    spinner.start()

    await question.ask(context)
    await first_step

async def issue_credential(loop, rel_did, cred_def_id):
    # input parameters for issue credential
    credential_name = 'Degree'
    credential_data = {
        'name': 'Alice Smith',
        'degree': 'Bachelors'
    }

    # constructor for the Issue Credential protocol
    issue = IssueCredential(rel_did, None, cred_def_id, credential_data, credential_name, 0, True)

    offer_sent = loop.create_future()
    cred_sent = loop.create_future()
    spinner = make_spinner('Wait for Connect.me to accept the Credential Offer')  # Console spinner

    # handler for 'sent` message when the offer for credential is sent
    async def send_offer_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == IssueCredential.SENT:
            offer_sent.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION, send_offer_handler)

    spinner.start()
    # request that credential is offered
    await issue.offer_credential(context)
    await offer_sent  # wait for sending of offer to connect.me user

    # handler for 'sent` message when the credential is sent
    async def send_cred_handler(msg_name, message):
        spinner.stop_and_persist('Done')
        print_message(msg_name, message)
        if msg_name == IssueCredential.SENT:
            cred_sent.set_result(None)
        else:
            non_handled(f'Message name is not handled - {msg_name}', message)

    # adds handler to the set of handlers
    handlers.add_handler(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION, send_cred_handler)

    spinner = make_spinner('waiting to send credential')  # Console spinner
    spinner.start()
    handlers.add_handler(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION, send_cred_handler)
    await cred_sent
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
        if msg_name == PresentProof.PRESENTATION_RESULT:
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

    config = ''
    # look for context on disk
    try:
        with open(CONFIG_PATH, 'r') as f:
            if console_yes_no(f'Reuse Verity Context (in {CONFIG_PATH})', True):
                config = f.read()
            else:
                await delete_wallet(json.dumps({'id': WALLET_NAME}), json.dumps({'key': WALLET_KEY}))
    except (FileNotFoundError, WalletNotFoundError):
        pass

    if config:
        context = await Context.create_with_config(config)
    else:
        context = await provision_agent()

    with open('verity-context.json', 'w') as f:
        f.write(context.to_json())

    await update_webhook_endpoint()

    print_object(context.to_json(indent=2), '>>>', 'Context Used:')

    with open('verity-context.json', 'w') as f:
        f.write(context.to_json())

    await update_configs()

    await issuer_identifier(loop)

    if not issuer_did:
        print('\nIssuer DID is not created. Performing Issuer setup now...')
        await setup_issuer(loop)
    else:
        print(f'Issuer DID:  {ANSII_GREEN}{issuer_did}{ANSII_RESET}')
        print(f'Issuer Verkey: {ANSII_GREEN}{issuer_verkey}{ANSII_RESET}')

async def provision_agent() -> str:
    global context
    token = None
    if console_yes_no('Provide Provision Token', True):
        token = console_input('Token', os.environ.get('TOKEN'))
        print(f'Using provision token: {ANSII_GREEN}{token}{ANSII_RESET}')

    verity_url = console_input(f'Verity Application Endpoint', os.environ.get('VERITY_SERVER'))
    print(f'Using Verity Application Endpoint Url: {ANSII_GREEN}{verity_url}{ANSII_RESET}')
    # create initial Context
    context = await Context.create(WALLET_NAME, WALLET_KEY, verity_url)

    # ask that an agent by provision (setup) and associated with created key pair
    try:
        response = await Provision(token).provision(context)
        return response
    except Exception as e:
        print(e)
        print('Provisioning failed! Likely causes:')
        print('- token not provided but Verity Endpoint requires it')
        print('- token provided but is invalid or expired')
        sys.exit(1)


async def update_webhook_endpoint():
    global context, port
    webhook_from_ctx: str = context.endpoint_url

    if not webhook_from_ctx:
        # Default to localhost on the default port
        webhook_from_ctx = f'http://localhost:{port}'

    webhook: str = console_input(f'Ngrok endpoint [{webhook_from_ctx}]', os.environ.get('WEBHOOK_URL'))

    if not webhook:
        webhook = webhook_from_ctx

    print(f'Using Webhook: {ANSII_GREEN}{webhook}{ANSII_RESET}')
    print()
    context.endpoint_url = webhook

    # request that verity application use specified webhook endpoint
    await UpdateEndpoint().update(context)


async def update_configs():
    handlers.add_handler('update-configs', '0.6', noop)
    configs = UpdateConfigs(INSTITUTION_NAME, LOGO_URL)
    await configs.update(context)


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
            # Do nothing. Just means we need to write the keys to the ledger. Checked for in setup()
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

    spinner = make_spinner('Waiting for setup to complete')  # Console spinner

    # handler for created issuer identifier message
    async def public_identifier_handler(msg_name, message):
        global issuer_did
        global issuer_verkey

        spinner.stop_and_persist('Done')

        if msg_name == IssuerSetup.PUBLIC_IDENTIFIER_CREATED:
            issuer_did = message['identifier']['did']
            issuer_verkey = message['identifier']['verKey']
            print(f'Issuer DID:  {ANSII_GREEN}{issuer_did}{ANSII_RESET}')
            print(f'Issuer Verkey: {ANSII_GREEN}{issuer_verkey}{ANSII_RESET}')
            print('The issuer DID and Verkey must be registered on the ledger.')
            print('Automated registration is currently unavailable')
            automated_registration = False # console_yes_no(f'Attempt automated registration via {ANSII_GREEN}https://selfserve.sovrin.org{ANSII_RESET}', True)
            if automated_registration:
                url = 'https://selfserve.sovrin.org/nym'
                payload = json.dumps({
                    'network': 'stagingnet',
                    'did': issuer_did,
                    'verkey': issuer_verkey,
                    'paymentaddr': ''
                })
                headers = {'Accept': 'application/json'}
                response = requests.request('POST', url, headers=headers, data=payload)
                if response.status_code != 200:
                    print('Something went wrong with contactig Sovrin portal')
                    print('Please add Issuer DID and Verkey to the ledger manually')
                    console_input('Press ENTER when DID is on ledger')
                else:
                    print(f'Got response from Sovrin portal: {ANSII_GREEN}{response.text}{ANSII_RESET}')
            else:
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
