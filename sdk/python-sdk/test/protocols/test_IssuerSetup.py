import pytest

from verity_sdk.protocols.IssuerSetup import IssuerSetup
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup


def test_init():
  IssuerSetup()

@pytest.mark.asyncio
async def test_create():
  context = await Context.create(await get_test_config())
  issuerSetup = IssuerSetup()
  issuerSetup.send = send_stub
  msg = await issuerSetup.create(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    IssuerSetup.MSG_FAMILY,
    IssuerSetup.MSG_FAMILY_VERSION,
    IssuerSetup.CREATE
  )
  assert msg['@id'] is not None

  await cleanup(context)
