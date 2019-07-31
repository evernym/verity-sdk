from typing import List
from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class PresentProof(Protocol):
  MSG_FAMILY = 'present-proof'
  MSG_FAMILY_VERSION = '0.1'

  # Messages
  PROOF_REQUEST = 'request'

  # Status
  PROOF_REQUEST_SENT_STATUS = 0
  PROOF_RECEIVED_STATUS = 1

  connection_id: str
  name: str
  proof_attrs: List[dict]
  revocation_interval: dict

  def __init__(self, connection_id: str, name: str, proof_attrs: List[dict], revocation_interval: dict = None):
    self.connection_id = connection_id
    self.name = name
    self.proof_attrs = proof_attrs
    self.revocation_interval = revocation_interval or {}
    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.PROOF_REQUEST: {
        '@type': PresentProof.get_message_type(self.PROOF_REQUEST),
        '@id': self.get_new_id(),
        'connectionId': self.connection_id,
        'proofRequest': {
          'name': self.name,
          'proofAttrs': self.proof_attrs,
          'revocationInterval': self.revocation_interval
        }
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(PresentProof.MSG_FAMILY, PresentProof.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(PresentProof.MSG_FAMILY, PresentProof.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(PresentProof.MSG_FAMILY, PresentProof.MSG_FAMILY_VERSION)

  async def request(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.PROOF_REQUEST])
