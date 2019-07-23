from typing import List
from src.utils import Context, uuid
from src.protocols.Protocol import Protocol

class WriteSchema(Protocol):
  MSG_FAMILY = "schema"
  MSG_FAMILY_VERSION = "0.1"

  # Messages
  WRITE_SCHEMA = "write"

  class STATUS():
    WRITE_SUCCESSFUL = 0

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
        '@type': self.get_message_type(self.WRITE_SCHEMA),
        '@id': self.get_new_id(),
        'schema': {
          'name': self.name,
          'version': self.version,
          'attrNames': self.attrs
        }
      }
    }

  async def write(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.WRITE_SCHEMA])
