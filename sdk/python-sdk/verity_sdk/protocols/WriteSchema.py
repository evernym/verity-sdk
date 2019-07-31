from typing import List

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type


class WriteSchema(Protocol):
  MSG_FAMILY = 'write-schema'
  MSG_FAMILY_VERSION = '0.1'

  # Messages
  WRITE_SCHEMA = 'write'

  # Status
  WRITE_SUCCESSFUL_STATUS = 0

  # Data members
  name: str
  version: str
  attrs: List[str]

  def __init__(self, name: str, version: str, *attrs: str):
    self.name = name
    self.version = version
    self.attrs = list(attrs)
    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.WRITE_SCHEMA: {
        '@type': WriteSchema.get_message_type(self.WRITE_SCHEMA),
        '@id': self.get_new_id(),
        'name': self.name,
        'version': self.version,
        'attrNames': self.attrs
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(WriteSchema.MSG_FAMILY, WriteSchema.MSG_FAMILY_VERSION)

  async def write(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.WRITE_SCHEMA])
