import json
from typing import Dict
from uuid import uuid4
from indy import crypto

from verity_sdk.utils import Context

MESSAGE_TYPE_DID = 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw'


def prepare_forward_message(did: str, message: bytes) -> str:
  return json.dumps({
    '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
    '@fwd': did,
    '@msg': json.loads(message.decode('utf-8'))
  })


async def pack_message_for_verity(context: Context, message: dict) -> bytes:
  agent_message = await crypto.pack_message(
    context.wallet_handle,
    json.dumps(message),
    [context.verity_pairwise_verkey],
    context.sdk_pairwise_verkey,
  )
  forward_message = prepare_forward_message(
    context.verity_pairwise_did,
    agent_message
  )
  return await crypto.pack_message(
    context.wallet_handle,
    forward_message,
    [context.verity_public_verkey],
    None
  )


async def unpack_forward_message(context: Context, message: bytes) -> Dict:
  unpacked_once_message = await unpack_message(context, message)
  return await unpack_message(
    context,
    json.dumps(unpacked_once_message['@msg']).encode('utf-8')
  )


async def unpack_message(context: Context, message: bytes) -> Dict:
  jwe: bytes = await crypto.unpack_message(
    context.wallet_handle,
    message
  )
  message = json.loads(jwe.decode('utf-8'))['message']
  return json.loads(message)


def uuid() -> str:
  return str(uuid4())


def get_message_type(msg_family: str, msg_family_version: str, msg_name: str) -> str:
  return '{};spec/{}/{}/{}'.format(MESSAGE_TYPE_DID, msg_family, msg_family_version, msg_name)


def get_problem_report_message_type(msg_family: str, msg_family_version: str) -> str:
  return get_message_type(msg_family, msg_family_version, 'problem-report')


def get_status_message_type(msg_family: str, msg_family_version: str) -> str:
  return get_message_type(msg_family, msg_family_version, 'status')
