import json
from uuid import uuid4 as uuid

import pytest
from indy import did, wallet

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import prepare_forward_message, pack_message_for_verity, \
  unpack_forward_message, get_message_type, get_problem_report_message_type, \
  MESSAGE_TYPE_DID, get_status_message_type
from verity_sdk.utils.Context import Context


async def get_test_config():
  test_config = {
    'walletName': str(uuid()),
    'walletKey': str(uuid()),
    'verityUrl': 'http://localhost:8080/agency/msg',
    'verityPublicVerkey': None,
    'verityPairwiseDID': None,
    'verityPairwiseVerkey': None,
    'sdkPairwiseVerkey': None,
    'endpointUrl': 'http://localhost:4000'
  }

  wallet_config = json.dumps({'id': test_config['walletName']})
  wallet_credentials = json.dumps({'key': test_config['walletKey']})
  await wallet.create_wallet(wallet_config, wallet_credentials)
  wallet_handle = await wallet.open_wallet(wallet_config, wallet_credentials)
  [_, their_public_verkey] = await did.create_and_store_my_did(wallet_handle, '{}')
  [their_did, their_verkey] = await did.create_and_store_my_did(wallet_handle, '{}')
  [_, my_verkey] = await did.create_and_store_my_did(wallet_handle, '{}')
  await wallet.close_wallet(wallet_handle)

  test_config['verityPublicVerkey'] = their_public_verkey
  test_config['verityPairwiseDID'] = their_did
  test_config['verityPairwiseVerkey'] = their_verkey
  test_config['sdkPairwiseVerkey'] = my_verkey

  return json.dumps(test_config)


async def cleanup(context: Context):
  if not context.wallet_closed:
    await context.close_wallet()
  await wallet.delete_wallet(context.wallet_config, context.wallet_credentials)


async def send_stub(context: Context, message: dict) -> bytes:
  return await Protocol.get_message(context, message)


@pytest.mark.asyncio
async def test_Context():
  test_config = await get_test_config()

  context = await Context.create(test_config)
  test_config = json.loads(test_config)
  assert context.wallet_name == test_config['walletName']
  assert context.wallet_key == test_config['walletKey']
  assert context.verity_url == test_config['verityUrl']
  assert context.verity_public_verkey == test_config['verityPublicVerkey']
  assert context.verity_pairwise_did == test_config['verityPairwiseDID']
  assert context.verity_pairwise_verkey == test_config['verityPairwiseVerkey']
  assert context.sdk_pairwise_verkey == test_config['sdkPairwiseVerkey']
  assert context.endpoint_url == test_config['endpointUrl']

  await cleanup(context)


def test_prepare_forward_message():
  test_did = '123'
  message = b'{"hello": "world"}'
  msg = prepare_forward_message(test_did, message)
  msg = json.loads(msg)

  assert msg['@type'] == 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD'
  assert msg['@fwd'] == test_did
  assert msg['@msg'] == json.loads(message.decode('utf-8'))

@pytest.mark.asyncio
async def test_pack_message_for_verity_and_unpack_forward_message():
  message = {'hello': 'world'}
  context = await Context.create(await get_test_config())
  packed_message = await pack_message_for_verity(context, message)
  unpacked_message = await unpack_forward_message(context, packed_message)
  assert unpacked_message['hello'] == 'world'

  await cleanup(context)


def test_get_message_type():
  msg_type: str = 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/status'
  assert get_message_type('credential', '0.1', 'status') == msg_type


def test_get_problem_report_message_type():
  assert get_problem_report_message_type('credential', '0.1') == \
         '{};spec/credential/0.1/problem-report'.format(MESSAGE_TYPE_DID)


def test_get_status_message_type():
  assert get_status_message_type('credential', '0.1') ==\
         '{};spec/credential/0.1/status'.format(MESSAGE_TYPE_DID)
