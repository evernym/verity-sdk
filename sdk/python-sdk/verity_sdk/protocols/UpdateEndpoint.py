from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.utils.Context import Context


class UpdateEndpoint(Protocol):
  MSG_FAMILY = 'configs'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  UPDATE_ENDPOINT = 'UPDATE_COM_METHOD'

  context: Context

  def __init__(self, context: Context):
    self.context = context
    self.define_messages()

  def define_messages(self):
    COM_METHOD_TYPE = 2

    self.messages = {
      self.UPDATE_ENDPOINT: {
        '@type': UpdateEndpoint.get_message_type(self.UPDATE_ENDPOINT),
        '@id': UpdateEndpoint.get_new_id(),
        'comMethod': {
          'id': 'webhook',
          'type': COM_METHOD_TYPE,
          'value': self.context.endpoint_url
        }
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(UpdateEndpoint.MSG_FAMILY, UpdateEndpoint.MSG_FAMILY_VERSION)

  async def update(self) -> bytes:
    return await self.send(self.context, self.messages[self.UPDATE_ENDPOINT])
