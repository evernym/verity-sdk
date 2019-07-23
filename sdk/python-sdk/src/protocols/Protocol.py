import abc
import json

from src.utils import Context, pack_message_for_verity, uuid
from src.transports import send_message

class Protocol:

  MSG_FAMILY = "none"
  MSG_FAMILY_VERSION = "0.0.0"
  MESSAGE_TYPE_DID = 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw'

  class STATUS():
    pass

  messages: dict

  @staticmethod
  async def get_message(context: Context, message: dict) -> bytes:
    return await pack_message_for_verity(context, message)

  @staticmethod
  async def send(context: Context, message: dict) -> bytes:
    message = await Protocol.get_message(context, message)
    send_message(context.verity_url, message)
    return message

  @staticmethod
  def get_new_id():
    return uuid()

  @staticmethod
  def get_message_type_complete(msg_family: str, msg_family_version: str, msg_name: str) -> str:
    return "{};spec/{}/{}/{}".format(Protocol.MESSAGE_TYPE_DID, msg_family, msg_family_version, msg_name)

  def get_message_type(self, msg_name: str) -> str:
    return Protocol.get_message_type_complete(self.MSG_FAMILY, self.MSG_FAMILY_VERSION, msg_name)

  def get_problem_report_message_type(self) -> str:
    return self.get_message_type("problem-report")

  def get_status_message_type(self) -> str:
    return self.get_message_type("status")

  @abc.abstractmethod
  def define_messages(self):
    pass

