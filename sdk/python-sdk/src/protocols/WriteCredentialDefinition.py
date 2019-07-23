
from src.utils import Context
from src.protocols.Protocol import Protocol

class WriteCredentialDefinition(Protocol):
  MSG_FAMILY = 'cred-def'
  MSG_FAMILY_VERSION = '0.1'

  # Messages
  WRITE_CRED_DEF = "write"

  class STATUS():
    WRITE_SUCCESSFUL = 0

  # Data members
  name: str
  schema_id: str
  tag: str
  revocation_details: dict

  def __init__(self, name: str, schema_id: str, tag: str = None, revocation_details: dict = None):
    self.name = name
    self.schema_id = schema_id
    self.tag = tag
    self.revocation_details = revocation_details
    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.WRITE_CRED_DEF: {
        '@type': self.get_message_type(self.WRITE_CRED_DEF),
        '@id': self.get_new_id(),
        'name': self.name,
        'schemaId': self.schema_id,
        'tag': self.tag,
        'revocationDetails': self.revocation_details
      }
    }

  async def write(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.WRITE_CRED_DEF])
