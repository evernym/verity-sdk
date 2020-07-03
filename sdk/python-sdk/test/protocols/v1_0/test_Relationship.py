import pytest

from test.test_utils import get_test_config
from verity_sdk.protocols.v1_0.Relationship import Relationship
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


def test_init():
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test',
        profile_url='profile_url'
    )

    assert relationship.label == 'test'
    assert relationship.for_relationship == 'RxRJCMe5XNqc9e9J1YPwhL'
    assert relationship.thread_id == '7a80285e-896c-45f6-b386-39ed7c49230c'
    assert relationship.profile_url == 'profile_url'

    relationship = Relationship(
        label=None
    )

    assert relationship.label == ''
    assert relationship.profile_url is None


@pytest.mark.asyncio
async def test_create():
    context = await Context.create_with_config(await get_test_config())
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test',
        profile_url='profile_url'
    )

    msg = relationship.create_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        Relationship.MSG_FAMILY,
        Relationship.MSG_FAMILY_VERSION,
        Relationship.CREATE
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['label'] == 'test'
    assert msg['profileUrl'] == 'profile_url'


@pytest.mark.asyncio
async def test_connection_invitation():
    context = await Context.create_with_config(await get_test_config())
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test'
    )

    msg = relationship.connection_invitation_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        Relationship.MSG_FAMILY,
        Relationship.MSG_FAMILY_VERSION,
        Relationship.CONNECTION_INVITATION
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['~for_relationship'] == 'RxRJCMe5XNqc9e9J1YPwhL'
