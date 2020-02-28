import json
import pytest

from src.utils import unpack_forward_message
from src.utils.Context import Context
from src.protocols.Protocol import Protocol
from ..test_utils import get_test_config, cleanup


@pytest.mark.asyncio
async def test_get_message():
    message = {'hello': 'world'}
    context = await Context.create(await get_test_config())
    packed_message = await Protocol.get_message(context, message)
    unpacked_message = json.dumps(await unpack_forward_message(context, packed_message))
    assert json.dumps(message) == unpacked_message
    await cleanup(context)


@pytest.mark.asyncio
async def test_get_thread_block():
    protocol = Protocol()
    block = protocol.get_thread_block()
    assert block['thid'] is not None
