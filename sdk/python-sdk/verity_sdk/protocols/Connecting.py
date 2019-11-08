from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class Connecting(Protocol):
  MSG_FAMILY = 'connecting'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  CREATE_CONNECTION = 'CREATE_CONNECTION'
  GET_STATUS = 'get-status'

  # Status
  AWAITING_RESPONSE_STATUS = 0
  INVITE_ACCEPTED_STATUS = 1

  source_id: str
  phone_number: str
  include_public_did: bool

  def __init__(self, source_id: str, phone_number: str = None, include_public_did: bool = False):
    self.source_id = source_id
    self.phone_number = phone_number
    self.include_public_did = include_public_did

    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.CREATE_CONNECTION: {
        '@type': Connecting.get_message_type(self.CREATE_CONNECTION),
        '@id': self.get_new_id(),
        'sourceId': self.source_id,
        'phoneNo': self.phone_number,
        'includePublicDID': self.include_public_did
      },
      self.GET_STATUS: {
        '@type': Connecting.get_message_type(self.GET_STATUS),
        '@id': self.get_new_id(),
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(Connecting.MSG_FAMILY, Connecting.MSG_FAMILY_VERSION)

  async def connect(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.CREATE_CONNECTION])

  async def status(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.GET_STATUS])
