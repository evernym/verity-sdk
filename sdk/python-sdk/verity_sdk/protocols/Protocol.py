import abc

from verity_sdk.utils import Context, pack_message_for_verity, uuid
from verity_sdk.transports import send_message


class Protocol:
  MSG_FAMILY = 'none'
  MSG_FAMILY_VERSION = '0.0.0'

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

  @abc.abstractmethod
  def define_messages(self):
    pass
