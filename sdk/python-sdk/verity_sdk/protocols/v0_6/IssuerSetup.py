from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


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
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER
        )

    def create_msg(self, _):
        msg = self._get_base_message(self.CREATE)
        msg = self._add_thread(msg)
        return msg

    async def create_msg_packed(self, context):
        return await self.get_message_bytes(context, self.create_msg(context))

    async def create(self, context):
        await self.send_message(context, await self.create_msg_packed(context))

    def current_public_identifier_msg(self, _):
        msg = self._get_base_message(self.CURRENT_PUBLIC_IDENTIFIER)
        msg = self._add_thread(msg)
        return msg

    async def current_public_identifier_msg_packed(self, context):
        return await self.get_message_bytes(context, self.current_public_identifier_msg(context))

    async def current_public_identifier(self, context):
        await self.send_message(context, await self.current_public_identifier_msg_packed(context))
