import json
import pytest

from verity_sdk.utils import unpack_forward_message
from verity_sdk.utils.Context import Context
from verity_sdk.protocols.Protocol import Protocol
from ..test_utils import get_test_config, cleanup


@pytest.mark.asyncio
async def test_get_message():
  message = {'hello': 'world'}
  context = await Context.create(await get_test_config())
  packed_message = await Protocol.get_message(context, message)
  unpacked_message = json.dumps(await unpack_forward_message(context, packed_message))
  assert json.dumps(message) == unpacked_message
  await cleanup(context)
