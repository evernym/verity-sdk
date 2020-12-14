import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v1_0.BasicMessage import BasicMessage
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Context import Context

for_relationship = 'some_did'
content = 'Are you trying to login to acme.com?'
sent_time = '12:45:00-000'
localization = 'en'

def test_init():
    basic_message = BasicMessage(for_relationship, None, content, sent_time, localization)

    assert basic_message.for_relationship == for_relationship
    assert basic_message.content == content
    assert basic_message.sent_time == sent_time
    assert basic_message.localization == localization


@pytest.mark.asyncio
async def test_message():
    context = await Context.create_with_config(await get_test_config())
    basic_message = BasicMessage(for_relationship, None, content, sent_time, localization)
    msg = basic_message.message_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        BasicMessage.MSG_FAMILY,
        BasicMessage.MSG_FAMILY_VERSION,
        BasicMessage.BASIC_MESSAGE
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['content'] == content
    assert msg['sent_time'] == sent_time
    assert msg['~l10n']['locale'] == localization

    await cleanup(context)
