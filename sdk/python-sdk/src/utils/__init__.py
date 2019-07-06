import json
from typing import Dict
from indy import crypto

from src.utils import Context

def prepare_forward_message(did: str, message: bytearray) -> str:
  return json.dumps({
    '@type': 'did:sov:123456789abcdefghi1234;spec/routing/0.6/FWD',
    '@fwd': did,
    '@msg': json.loads(message.decode('utf-8'))
  })

async def pack_message_for_verity(context: Context, message: str) -> bytearray:
  agent_message = await crypto.pack_message(
    context.wallet_handle,
    json.dumps([context.verity_pairwise_verkey]),
    context.sdk_pairwise_verkey,
    message.encode('utf-8')
  )
  forward_message = prepare_forward_message(
    context.verity_pairwise_did,
    agent_message
  )
  return await crypto.pack_message(
    context.wallet_handle,
    json.dumps([context.verity_public_verkey]),
    None,
    bytes(forward_message)
  )

# FIXME: Why do we need both of these functions? They basically do the same thing!
async def unpack_message_from_verity(context: Context, message: bytearray) -> Dict:
  jwe: bytearray = crypto.unpack_message(
    context.wallet_handle,
    message
  )
  message = json.loads(jwe.decode('utf-8'))['message']
  return json.loads(message)


async def unpack_forward_msg(context: Context, message: Dict) -> Dict:
  jwe: bytearray = crypto.unpack_message(
    context.wallet_handle,
    json.dumps(message).encode('utf-8')
  )
  message = json.loads(jwe.decode('utf-8'))['message']
  return json.loads(message)
