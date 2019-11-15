import pytest

from verity_sdk.protocols.QuestionAnswer import QuestionAnswer
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup


for_relationship = 'some_did'
question_text = 'Are you trying to login to acme.com?'
question_detail = 'IP Address: 56.24.11.126'
valid_responses = ['Yes', 'No, that\'s not me!']
signature_required = True


def test_init():
  question_answer = QuestionAnswer(for_relationship, question_text, question_detail, valid_responses, signature_required)

  assert question_answer.for_relationship == for_relationship
  assert question_answer.question_text == question_text
  assert question_answer.question_detail == question_detail
  assert question_answer.valid_responses == valid_responses
  assert question_answer.signature_required == signature_required


@pytest.mark.asyncio
async def test_ask():
  context = await Context.create(await get_test_config())
  question_answer = QuestionAnswer(for_relationship, question_text, question_detail, valid_responses, signature_required)
  question_answer.send = send_stub
  msg = await question_answer.ask(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    QuestionAnswer.MSG_FAMILY,
    QuestionAnswer.MSG_FAMILY_VERSION,
    QuestionAnswer.ASK_QUESTION
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
  context = await Context.create(await get_test_config())
  question_answer = QuestionAnswer(for_relationship, question_text, question_detail, valid_responses, signature_required)
  question_answer.send = send_stub
  msg = await question_answer.status(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    QuestionAnswer.MSG_FAMILY,
    QuestionAnswer.MSG_FAMILY_VERSION,
    QuestionAnswer.GET_STATUS
  )
  assert msg['@id'] is not None
  assert msg['~for_relationship'] == for_relationship
  assert msg['~thread'] is not None
  assert msg['~thread']['thid'] is not None

  await cleanup(context)
