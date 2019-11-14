from verity_sdk.utils import Context, get_message_type
from verity_sdk.protocols.Protocol import Protocol


class IssuerSetup(Protocol):
  MSG_FAMILY = 'issuer-setup'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  CREATE = 'create'


  def __init__(self):
    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.CREATE: {
        '@type': IssuerSetup.get_message_type(self.CREATE),
        '@id': self.get_new_id(),
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(IssuerSetup.MSG_FAMILY, IssuerSetup.MSG_FAMILY_VERSION, msg_name)

  async def create(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.CREATE])
