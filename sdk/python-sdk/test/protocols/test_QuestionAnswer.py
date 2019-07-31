import pytest

from verity_sdk.protocols.QuestionAnswer import QuestionAnswer
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

NONCE_LENGTH = 64

connection_id = '12345'
notification_title = 'Question from Acme'
question_text = 'Are you trying to login to acme.com?'
question_detail = 'IP Address: 56.24.11.126'
valid_responses = ['Yes', 'No, that\'s not me!']


def test_init():
  question_answer = QuestionAnswer(connection_id, notification_title, question_text, question_detail, valid_responses)

  assert question_answer.connection_id == connection_id
  assert question_answer.notification_title == notification_title
  assert question_answer.question_text == question_text
  assert question_answer.question_detail == question_detail
  assert question_answer.valid_responses[0]['text'] == valid_responses[0]
  assert len(question_answer.valid_responses[0]['nonce']) == NONCE_LENGTH
  assert question_answer.valid_responses[1]['text'] == valid_responses[1]
  assert len(question_answer.valid_responses[1]['nonce']) == NONCE_LENGTH


def test_format_valid_responses():
  formatted_valid_responses = QuestionAnswer.format_valid_responses('Question text', valid_responses)
  assert formatted_valid_responses[0]['text'] == valid_responses[0]
  assert len(formatted_valid_responses[0]['nonce']) == NONCE_LENGTH
  assert formatted_valid_responses[1]['text'] == valid_responses[1]
  assert len(formatted_valid_responses[1]['nonce']) == NONCE_LENGTH


def test_get_nonce():
  nonce = QuestionAnswer.get_nonce('question text', 'valid response')
  assert len(nonce) == NONCE_LENGTH


@pytest.mark.asyncio
async def test_ask():
  context = await Context.create(await get_test_config())
  question_answer = QuestionAnswer(connection_id, notification_title, question_text, question_detail, valid_responses)
  question_answer.send = send_stub
  msg = await question_answer.ask(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/question-answer/0.1/question'.format(MESSAGE_TYPE_DID)
  assert msg['@id'] is not None
  assert msg['connectionId'] == connection_id
  assert msg['question']['notification_title'] == notification_title
  assert msg['question']['question_text'] == question_text
  assert msg['question']['question_detail'] == question_detail
  assert msg['question']['valid_responses'][0]['text'] == valid_responses[0]
  assert msg['question']['valid_responses'][0]['nonce']
  assert msg['question']['valid_responses'][1]['text'] == valid_responses[1]
  assert msg['question']['valid_responses'][1]['nonce']
  assert msg['question']['@timing'] is None
  assert msg['question']['external_links'] is None

  await cleanup(context)
