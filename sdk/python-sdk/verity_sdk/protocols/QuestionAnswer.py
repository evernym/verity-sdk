from typing import List
from secrets import token_bytes

from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class QuestionAnswer(Protocol):
  MSG_FAMILY = 'questionanswer'
  MSG_FAMILY_VERSION = '1.0'

  # Messages
  ASK_QUESTION = 'ask-question'
  GET_STATUS = 'get-status'

  # Status
  QUESTION_SENT_STATUS = 0
  QUESTION_ANSWERED_STATUS = 1

  for_relationship: str
  question_text: str
  question_detail: str
  valid_responses: List[str]
  signature_required: bool


  def __init__(self,
               for_relationship: str,
               question_text: str,
               question_detail: str,
               valid_responses: List[str],
               signature_required: bool = True):
    self.for_relationship = for_relationship
    self.question_text = question_text
    self.question_detail = question_detail
    self.valid_responses = valid_responses
    self.signature_required = signature_required

    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.ASK_QUESTION: {
        '@type': QuestionAnswer.get_message_type(self.ASK_QUESTION),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
        'text': self.question_text,
        'detail': self.question_detail,
        'valid_responses': self.valid_responses,
        'signature_required': self.signature_required,
        # '@timing': None,  # TODO add support for @timing
        # 'external_links': None  # TODO add support for external_links
      },
      self.GET_STATUS: {
        '@type': QuestionAnswer.get_message_type(self.GET_STATUS),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(QuestionAnswer.MSG_FAMILY, QuestionAnswer.MSG_FAMILY_VERSION)

  @staticmethod
  def get_random_bytes():
    NUM_BYTES = 32
    return token_bytes(NUM_BYTES)

  async def ask(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.ASK_QUESTION])

  async def status(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.GET_STATUS])
