
__copyright__ = "COPYRIGHT 2013-2019, ALL RIGHTS RESERVED, EVERNYM INC."

import asyncio
import json
import random
import sys

from aiohttp import web

from verity_sdk.handlers import Handlers, AddHandler
from verity_sdk.protocols.Connecting import Connecting
from verity_sdk.protocols.IssueCredential import IssueCredential
from verity_sdk.protocols.PresentProof import PresentProof
from verity_sdk.protocols.QuestionAnswer import QuestionAnswer
from verity_sdk.protocols.UpdateEndpoint import UpdateEndpoint
from verity_sdk.protocols.WriteCredentialDefinition import WriteCredentialDefinition
from verity_sdk.protocols.WriteSchema import WriteSchema
from verity_sdk.utils.Context import Context

context = None
handlers = Handlers()
routes = web.RouteTableDef()

async def example():
  global context
  context = await Context.create(get_config())
  await UpdateEndpoint(context).update()
  print("endpoint updated")

  connection_id: str
  cred_def_id: str

  connecting: Connecting = Connecting("my institution id", use_public_did=True)
  await connecting.connect(context)

  @AddHandler(handlers, message_type=Connecting.get_status_message_type(),
              message_status=Connecting.AWAITING_RESPONSE_STATUS)
  async def print_invite_details(msg: dict) -> None:
    invite_details = msg['content']
    print('Invite Details: {}'.format(invite_details))

    # write to file for integration tests
    with open('example/inviteDetails.json', 'w') as outfile:
      outfile.write(invite_details)

  # You can also add handlers like this
  # handlers.add_handler(Connecting.get_status_message_type(), Connecting.AWAITING_RESPONSE_STATUS, print_invite_details)

  @AddHandler(handlers, message_type=Connecting.get_status_message_type(),
              message_status=Connecting.INVITE_ACCEPTED_STATUS)
  async def send_question(msg: dict) -> None:
    print("Connection accepted!")

    global connection_id
    connection_id = msg['content']
    question = QuestionAnswer(
      connection_id,
      "Challenge Question",
      "Hi Alice, how are you today?",
      " ",
      ["Great!", "Not so good."])
    await question.ask(context)

  @AddHandler(handlers, message_type=QuestionAnswer.get_status_message_type(),
              message_status=QuestionAnswer.QUESTION_ANSWERED_STATUS)
  async def write_schema_to_ledger(msg: dict) -> None:
    print("Question Answered: {}".format(msg['content']))

    write_schema = WriteSchema("Test Schema", get_random_version(), "name", "degree")
    await write_schema.write(context)

  @AddHandler(handlers, message_type=WriteSchema.get_status_message_type(),
              message_status=WriteSchema.WRITE_SUCCESSFUL_STATUS)
  async def write_cred_def_to_ledger(msg: dict) -> None:
    write_cred_def = WriteCredentialDefinition(name="Test Credential Definition", schema_id=msg['content'],
                                               tag="latest", revocation_details={'support_revocation': False})
    await write_cred_def.write(context)

  @AddHandler(handlers, message_type=WriteCredentialDefinition.get_status_message_type(),
              message_status=WriteCredentialDefinition.WRITE_SUCCESSFUL_STATUS)
  async def send_credential(msg: dict) -> None:
    global cred_def_id
    global connection_id
    cred_def_id = msg['content']
    issue_credential = IssueCredential(
      connection_id,
      name="Degree",
      cred_def_id=cred_def_id,
      credential_values={'name': 'John', 'degree': 'Bachelors of Science'},
      price=0)
    await issue_credential.issue(context)

  @AddHandler(handlers, message_type=IssueCredential.get_status_message_type(),
              message_status=IssueCredential.OFFER_ACCEPTED_BY_USER_STATUS)
  async def print_credential_status(msg: dict) -> None:
    print("User has accepted the credential offer. Verity is now sending the Credential")

  @AddHandler(handlers, message_type=IssueCredential.get_status_message_type(),
              message_status=IssueCredential.CREDENTIAL_SENT_TO_USER_STATUS)
  async def send_proof_request(msg: dict) -> None:
    global cred_def_id
    global connection_id
    present_proof = PresentProof(connection_id, name="Who are you?", proof_attrs=get_proof_attrs(cred_def_id))
    await present_proof.request(context)

  @AddHandler(handlers, message_type=PresentProof.get_status_message_type(),
              message_status=PresentProof.PROOF_RECEIVED_STATUS)
  async def print_proof(msg: dict) -> None:
    print("Proof Accepted")
    print(json.dumps(msg))
    sys.exit(0)

  async def default_handler(msg: dict):
    print("New message from verity: {}".format(json.dumps(msg)))

  handlers.add_default_handler(default_handler)

  async def problem_report_handler(msg: dict) -> None:
    print("New problem report from verity: {}".format(json.dumps(msg)))

  handlers.add_problem_report_handler(problem_report_handler)


def get_config():
  with open('example/verityConfig.json', 'r') as outfile:
    return outfile.read()


def get_random_version():
  return '{}.{}.{}'.format(get_random_int(), get_random_int(), get_random_int())


def get_random_int():
  random.seed()
  return random.randrange(0, 1000)


def get_proof_attrs(cred_def_id: str):
  return [
    {'name': 'name', 'restrictions': [{'issuer_did': get_issuer_did(cred_def_id)}]}
  ]


def get_issuer_did(cred_def_id: str):
  return cred_def_id.split(':')[0]


@routes.post('/')
async def endpoint_handler(request):
  try:
    await handlers.handle_message(context, await request.read())
    return web.Response(text="Success")
  except Exception as e:
    return web.Response(text=str(e))


async def main(loop):
  port = 4000
  app = web.Application(loop=loop)
  app.add_routes(routes)
  await loop.create_server(app.make_handler(), '0.0.0.0', port)
  print("Listening on port {}".format(port))
  await loop.create_task(example())


if __name__ == '__main__':
  loop = asyncio.get_event_loop()
  loop.run_until_complete(main(loop))
  loop.run_forever()
