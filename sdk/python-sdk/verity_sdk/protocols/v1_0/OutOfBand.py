from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class OutOfBand(Protocol):
    MSG_FAMILY = 'out-of-band'
    MSG_FAMILY_VERSION = '1.0'

    REUSE = 'reuse'

    def __init__(self,
                 for_relationship: str = None,
                 invite_url: str = None):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER
        )
        self.for_relationship = for_relationship
        self.invite_url = invite_url

    def reuse_msg(self, _):
        msg = self._get_base_message(self.REUSE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)

        msg['inviteUrl'] = self.invite_url
        return msg

    async def reuse_msg_packed(self, context):
        return await self.get_message_bytes(context, self.reuse_msg(context))

    async def reuse(self, context):
        await self.send_message(context, await self.reuse_msg_packed(context))
