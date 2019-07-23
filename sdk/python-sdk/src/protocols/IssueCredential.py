from src.utils import Context
from src.protocols.Protocol import Protocol

class IssueCredential(Protocol):
    MSG_FAMILY = 'issue-credential'
    MSG_FAMILY_VERSION = '0.1'

    # Messages
    ISSUE_CREDENTIAL = 'issue-credential'

    class STATUS():
        OFFER_SENT_TO_USER = 0
        OFFER_ACCEPTED_BY_USER = 1
        CREDENTIAL_SENT_TO_USER = 2

    connection_id: str
    name: str
    cred_def_id: str
    credential_values: dict
    price: int

    def __init__(self, connection_id: str, name: str, cred_def_id: str, credential_values: dict, price: int):
        self.connection_id = connection_id
        self.name = name
        self.cred_def_id = cred_def_id
        self.credential_values = credential_values
        self.price = price

        self.define_messages()

    def define_messages(self):
        self.messages = {
            self.ISSUE_CREDENTIAL: {
                '@type': self.get_message_type(self.ISSUE_CREDENTIAL),
                '@id': self.get_new_id(),
                'connectionId': self.connection_id,
                'credentialData': {
                    'id': self.get_new_id(),
                    'name': self.name,
                    'credDefId': self.cred_def_id,
                    'credentialValues': self.credential_values,
                    'price': self.price
                },
            }
        }

    async def issue(self, context: Context) -> bytes:
        return await self.send(context, self.messages[self.ISSUE_CREDENTIAL])