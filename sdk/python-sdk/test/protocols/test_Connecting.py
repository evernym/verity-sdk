import pytest

from verity_sdk.protocols.Connecting import Connecting
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

source_id = '12345'
phone_number = '1234357890'
include_public_did = True


def test_init():
  connecting = Connecting(source_id, phone_number, include_public_did)

  assert connecting.source_id == source_id
  assert connecting.phone_number == phone_number
  assert connecting.include_public_did == include_public_did
  assert len(connecting.messages) == 2


@pytest.mark.asyncio
async def test_connect():
  context = await Context.create(await get_test_config())
  connecting = Connecting(source_id, phone_number, include_public_did)
  connecting.send = send_stub
  msg = await connecting.connect(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    Connecting.MSG_FAMILY,
    Connecting.MSG_FAMILY_VERSION,
    Connecting.CREATE_CONNECTION
  )
  assert msg['@id'] is not None
  assert msg['sourceId'] == source_id
  assert msg['phoneNo'] == phone_number
  assert msg['includePublicDID'] == include_public_did

  await cleanup(context)

@pytest.mark.asyncio
async def test_status():
  context = await Context.create(await get_test_config())
  connecting = Connecting(source_id, phone_number, include_public_did)
  connecting.send = send_stub
  msg = await connecting.status(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    Connecting.MSG_FAMILY,
    Connecting.MSG_FAMILY_VERSION,
    Connecting.GET_STATUS
  )
  assert msg['@id'] is not None

  await cleanup(context)
