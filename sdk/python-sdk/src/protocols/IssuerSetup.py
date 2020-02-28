from src.utils import Context, get_message_type
from src.protocols.Protocol import Protocol


class IssuerSetup(Protocol):
    MSG_FAMILY = 'issuer-setup'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    CREATE = 'create'
    PUBLIC_IDENTIFIER = 'public-identifier'
    CURRENT_PUBLIC_IDENTIFIER = 'current-public-identifier'
    PUBLIC_IDENTIFIER_CREATED = 'public-identifier-created'
    PROBLEM_REPORT = 'problem-report'

    def __init__(self):
        super().__init__()
        self.define_messages()

    def define_messages(self):
        self.messages = {
            self.CREATE: {
                '@type': IssuerSetup.get_message_type(self.CREATE),
                '@id': self.get_new_id(),
            },
            self.CURRENT_PUBLIC_IDENTIFIER: {
              '@type': IssuerSetup.get_message_type(self.CURRENT_PUBLIC_IDENTIFIER),
              '@id': self.get_new_id(),
            }
        }

    @staticmethod
    def get_message_type(msg_name: str) -> str:
        return get_message_type(IssuerSetup.MSG_FAMILY, IssuerSetup.MSG_FAMILY_VERSION, msg_name)

    async def create(self, context: Context) -> bytes:
        return await self.send(context, self.messages[self.CREATE])

    async def current_public_identifier(self, context: Context) -> bytes:
        return await self.send(context, self.messages[self.CURRENT_PUBLIC_IDENTIFIER])
