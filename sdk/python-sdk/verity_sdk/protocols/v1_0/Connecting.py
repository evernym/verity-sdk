from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class Connecting(Protocol):
    MSG_FAMILY = "connections"
    MSG_FAMILY_VERSION = "1.0"

    ACCEPT_INVITE = "accept"
    STATUS = "status"

    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 label: str = None,
                 base64_invite_url: str = None):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )

        self.for_relationship = for_relationship
        self.label = label
        self.base64_invite_url = base64_invite_url

    def accept_msg(self):
        msg = self._get_base_message(self.ACCEPT_INVITE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['label'] = self.label
        msg['invite_url'] = self.base64_invite_url
        return msg

    async def accept_msg_packed(self, context):
        return await self.get_message_bytes(context, self.accept_msg())

    async def accept(self, context):
        await self.send_message(context, await self.accept_msg_packed(context))

    def status_msg(self):
        msg = self._get_base_message(self.STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, self.status_msg())

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
