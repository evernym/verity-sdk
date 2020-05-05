import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v0_6.IssuerSetup import IssuerSetup
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


def test_init():
    IssuerSetup()


@pytest.mark.asyncio
async def test_create():
    context = await Context.create_with_config(await get_test_config())
    issuer_setup = IssuerSetup()
    msg = issuer_setup.create_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        IssuerSetup.MSG_FAMILY,
        IssuerSetup.MSG_FAMILY_VERSION,
        IssuerSetup.CREATE
    )
    assert msg['@id'] is not None

    await cleanup(context)
