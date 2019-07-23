import pytest

from src.protocols.Connecting import Connecting
from src.utils.Context import Context
from src.utils import unpack_forward_message
from test.test_utils import get_test_config, send_stub, cleanup

source_id = '12345'
phone_number = '1234357890'
use_public_did = True

def test_init():
    connecting = Connecting(source_id, phone_number, use_public_did)

    assert connecting.source_id == source_id
    assert connecting.phone_number == phone_number
    assert connecting.use_public_did == use_public_did
    assert len(connecting.messages) == 1


@pytest.mark.asyncio
async def test_connect():
    context = await Context.create(await get_test_config())
    connecting = Connecting(source_id, phone_number, use_public_did)
    connecting.send = send_stub
    msg = await connecting.connect(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/connecting/0.1/CREATE_CONNECTION'.format(Connecting.MESSAGE_TYPE_DID)
    assert msg['@id']
    assert msg['connectionDetail']['sourceId'] == source_id
    assert msg['connectionDetail']['phoneNo'] == phone_number
    assert msg['connectionDetail']['usePublicDid'] == use_public_did

    await cleanup(context)