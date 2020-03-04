import pytest

from test.test_utils import get_test_config, send_stub, cleanup
from verity_sdk.protocols.IssuerSetup import IssuerSetup
from verity_sdk.utils import unpack_forward_message, EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context



def test_init():
    IssuerSetup()


@pytest.mark.asyncio
async def test_create():
    context = await Context.create_with_config(await get_test_config())
    issuer_setup = IssuerSetup()
    issuer_setup.send = send_stub
    msg = await issuer_setup.create(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        IssuerSetup.MSG_FAMILY,
        IssuerSetup.MSG_FAMILY_VERSION,
        IssuerSetup.CREATE
    )
    assert msg['@id'] is not None

    await cleanup(context)
