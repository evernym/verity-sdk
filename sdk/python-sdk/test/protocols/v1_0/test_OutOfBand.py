import pytest

from test.test_utils import get_test_config
from verity_sdk.protocols.v1_0.OutOfBand import OutOfBand
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


def test_init():
    relationship = OutOfBand(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        invite_url='test_invite_url'
    )

    assert relationship.for_relationship == 'RxRJCMe5XNqc9e9J1YPwhL'
    assert relationship.invite_url == 'test_invite_url'


@pytest.mark.asyncio
async def test_reuse():
    context = await Context.create_with_config(await get_test_config())
    relationship = OutOfBand(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        invite_url='test_invite_url'
    )

    msg = relationship.reuse_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        OutOfBand.MSG_FAMILY,
        OutOfBand.MSG_FAMILY_VERSION,
        OutOfBand.REUSE
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['inviteUrl'] == 'test_invite_url'
