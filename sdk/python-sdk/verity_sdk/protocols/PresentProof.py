from typing import List
from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class PresentProof(Protocol):
  MSG_FAMILY = 'present-proof'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  PROOF_REQUEST = 'request'
  GET_STATUS = 'get-status'

  # Status
  PROOF_REQUEST_SENT_STATUS = 0
  PROOF_RECEIVED_STATUS = 1

  for_relationship: str
  name: str
  proof_attrs: List[dict]
  proof_predicates: List[dict]
  revocation_interval: dict

  def __init__(self,
               for_relationship: str,
               name: str,
               proof_attrs: List[dict],
               proof_predicates: List[dict] = None,
               revocation_interval: dict = None):
    self.for_relationship = for_relationship
    self.name = name
    self.proof_attrs = proof_attrs
    self.proof_predicates = proof_predicates
    self.revocation_interval = revocation_interval or {}
    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.PROOF_REQUEST: {
        '@type': PresentProof.get_message_type(self.PROOF_REQUEST),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
        'name': self.name,
        'proofAttrs': self.proof_attrs,
        'proofPredicates': self.proof_predicates,
        'revocationInterval': self.revocation_interval
      },
      self.GET_STATUS: {
        '@type': PresentProof.get_message_type(self.GET_STATUS),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
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

  async def status(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.GET_STATUS])
