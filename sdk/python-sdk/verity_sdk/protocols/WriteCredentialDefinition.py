from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class WriteCredentialDefinition(Protocol):
  MSG_FAMILY = 'write-cred-def'
  MSG_FAMILY_VERSION = '0.1'

  # Messages
  WRITE_CRED_DEF = 'write'

  # Status
  WRITE_SUCCESSFUL_STATUS = 0

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
        '@type': WriteCredentialDefinition.get_message_type(self.WRITE_CRED_DEF),
        '@id': self.get_new_id(),
        'name': self.name,
        'schemaId': self.schema_id,
        'tag': self.tag,
        'revocationDetails': self.revocation_details
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(WriteCredentialDefinition.MSG_FAMILY, WriteCredentialDefinition.MSG_FAMILY_VERSION,
                            msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(WriteCredentialDefinition.MSG_FAMILY,
                                           WriteCredentialDefinition.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(WriteCredentialDefinition.MSG_FAMILY, WriteCredentialDefinition.MSG_FAMILY_VERSION)

  async def write(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.WRITE_CRED_DEF])
