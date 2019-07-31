from typing import List
from secrets import token_bytes
from hashlib import sha3_256

from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class QuestionAnswer(Protocol):
  MSG_FAMILY = 'question-answer'
  MSG_FAMILY_VERSION = '0.1'

  # Messages
  QUESTION = 'question'

  # Status
  QUESTION_SENT_STATUS = 0
  QUESTION_ANSWERED_STATUS = 1

  connection_id: str
  notification_title: str
  question_text: str
  question_detail: str
  valid_responses: List[dict]

  def __init__(self, connection_id: str, notification_title: str, question_text: str, question_detail: str,
               valid_responses: List[str]):
    self.connection_id = connection_id
    self.notification_title = notification_title
    self.question_text = question_text
    self.question_detail = question_detail
    self.valid_responses = self.format_valid_responses(question_text, valid_responses)

    self.define_messages()

  # TODO: If Verity is just sending the response back, it should be adding the nonce field as well
  @staticmethod
  def format_valid_responses(question_text, valid_responses) -> List[dict]:
    formatted_valid_responses = []

    for valid_response in valid_responses:
      formatted_valid_response = {
        'text': valid_response,
        'nonce': QuestionAnswer.get_nonce(question_text, valid_response)
      }

      formatted_valid_responses.append(formatted_valid_response)

    return formatted_valid_responses

  def define_messages(self):
    self.messages = {
      self.QUESTION: {
        '@type': QuestionAnswer.get_message_type(self.QUESTION),
        '@id': self.get_new_id(),
        'connectionId': self.connection_id,
        'question': {
          'notification_title': self.notification_title,
          'question_text': self.question_text,
          'question_detail': self.question_detail,
          'valid_responses': self.valid_responses,
          '@timing': None,  # TODO add support for @timing
          'external_links': None  # TODO add support for external_links
        }
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
  def get_nonce(question_text: str, valid_response: str) -> str:
    sha3_hash = sha3_256()
    sha3_hash.update(question_text.encode('utf-8'))
    sha3_hash.update(valid_response.encode('utf-8'))
    sha3_hash.update(QuestionAnswer.get_random_bytes())
    return sha3_hash.digest().hex()

  @staticmethod
  def get_random_bytes():
    NUM_BYTES = 32
    return token_bytes(NUM_BYTES)

  async def ask(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.QUESTION])
