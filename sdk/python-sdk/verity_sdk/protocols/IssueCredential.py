from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.protocols.Protocol import Protocol


class IssueCredential(Protocol):
  MSG_FAMILY = 'issue-credential'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  OFFER_CREDENTIAL = 'send-offer'
  ISSUE_CREDENTIAL = 'issue-credential'
  GET_STATUS = 'get-status'

  # Status
  OFFER_SENT_TO_USER_STATUS = 0
  OFFER_ACCEPTED_BY_USER_STATUS = 1
  CREDENTIAL_SENT_TO_USER_STATUS = 2

  for_relationship: str
  name: str
  cred_def_id: str
  credential_values: dict
  price: str

  def __init__(self, for_relationship: str, name: str, cred_def_id: str, credential_values: dict, price: str):
    self.for_relationship = for_relationship
    self.name = name
    self.cred_def_id = cred_def_id
    self.credential_values = credential_values
    self.price = price

    self.define_messages()

  def define_messages(self):
    self.messages = {
      self.OFFER_CREDENTIAL: {
        '@type': IssueCredential.get_message_type(self.OFFER_CREDENTIAL),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
        'name': self.name,
        'credDefId': self.cred_def_id,
        'credentialValues': self.credential_values,
        'price': self.price
      },
      self.ISSUE_CREDENTIAL: {
        '@type': IssueCredential.get_message_type(self.ISSUE_CREDENTIAL),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
      },
      self.GET_STATUS: {
        '@type': IssueCredential.get_message_type(self.GET_STATUS),
        '@id': self.get_new_id(),
        '~for_relationship': self.for_relationship,
        '~thread': self.get_thread_block(),
      }
    }

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def get_problem_report_message_type() -> str:
    return get_problem_report_message_type(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION)

  @staticmethod
  def get_status_message_type() -> str:
    return get_status_message_type(IssueCredential.MSG_FAMILY, IssueCredential.MSG_FAMILY_VERSION)

  async def offer_credential(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.OFFER_CREDENTIAL])

  async def issue_credential(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.ISSUE_CREDENTIAL])

  async def status(self, context: Context) -> bytes:
    return await self.send(context, self.messages[self.GET_STATUS])
