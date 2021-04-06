import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v1_0.CommittedAnswer import CommittedAnswer
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Context import Context

for_relationship = 'some_did'
question_text = 'Are you trying to login to acme.com?'
question_detail = 'IP Address: 56.24.11.126'
valid_responses = ['Yes', 'No, that\'s not me!']
signature_required = True


def test_init():
    committed_answer = CommittedAnswer(for_relationship, None, question_text, question_detail, valid_responses,
                                       signature_required)

    assert committed_answer.for_relationship == for_relationship
    assert committed_answer.question == question_text
    assert committed_answer.descr == question_detail
    assert committed_answer.valid_responses == valid_responses
    assert committed_answer.signature_required == signature_required


@pytest.mark.asyncio
async def test_ask():
    context = await Context.create_with_config(await get_test_config())
    committed_answer = CommittedAnswer(for_relationship, None, question_text, question_detail, valid_responses,
                                       signature_required)
    msg = committed_answer.ask_msg(context)

    assert msg['@type'] == '{}/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        CommittedAnswer.MSG_FAMILY,
        CommittedAnswer.MSG_FAMILY_VERSION,
        CommittedAnswer.ASK_QUESTION
    )
    assert msg['@id'] is not None
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['text'] == question_text
    assert msg['detail'] == question_detail
    assert msg['valid_responses'] == valid_responses
    assert msg['signature_required'] == signature_required

    await cleanup(context)


@pytest.mark.asyncio
async def test_status():
    context = await Context.create_with_config(await get_test_config())
    committed_answer = CommittedAnswer(for_relationship, None, question_text, question_detail, valid_responses,
                                       signature_required)
    msg = committed_answer.status_msg(context)

    assert msg['@type'] == '{}/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        CommittedAnswer.MSG_FAMILY,
        CommittedAnswer.MSG_FAMILY_VERSION,
        CommittedAnswer.GET_STATUS
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None

    await cleanup(context)
