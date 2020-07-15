import pytest

from test.test_utils import get_test_config
from verity_sdk.protocols.v1_0.Relationship import Relationship, GoalsList
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


def test_init():
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test',
        logo_url='logo_url'
    )

    assert relationship.label == 'test'
    assert relationship.for_relationship == 'RxRJCMe5XNqc9e9J1YPwhL'
    assert relationship.thread_id == '7a80285e-896c-45f6-b386-39ed7c49230c'
    assert relationship.logo_url == 'logo_url'

    relationship = Relationship(
        label=None
    )

    assert relationship.label == ''
    assert relationship.logo_url is None


@pytest.mark.asyncio
async def test_create():
    context = await Context.create_with_config(await get_test_config())
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test',
        logo_url='logo_url'
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
    assert msg['logoUrl'] == 'logo_url'


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


@pytest.mark.asyncio
async def test_out_of_band_invitation():
    context = await Context.create_with_config(await get_test_config())
    relationship = Relationship(
        for_relationship='RxRJCMe5XNqc9e9J1YPwhL',
        thread_id='7a80285e-896c-45f6-b386-39ed7c49230c',
        label='test'
    )

    msg = relationship.out_of_band_invitation_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        Relationship.MSG_FAMILY,
        Relationship.MSG_FAMILY_VERSION,
        Relationship.OUT_OF_BAND_INVITATION
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['~for_relationship'] == 'RxRJCMe5XNqc9e9J1YPwhL'
    assert msg['goalCode'] == GoalsList.P2P_MESSAGING.value.code
    assert msg['goal'] == GoalsList.P2P_MESSAGING.value.name
