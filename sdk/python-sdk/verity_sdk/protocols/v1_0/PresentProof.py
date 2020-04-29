from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class PresentProof(Protocol):
    MSG_FAMILY = "present-proof"
    MSG_FAMILY_VERSION = "1.0"

    PROOF_REQUEST = "request"
    STATUS = "status"
    REJECT = "reject"

    def __init__(self,
                 for_relationship: str,
                 name: str,
                 proof_attrs=None,
                 proof_predicates=None,
                 thread_id: str = None):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )
        self.for_relationship = for_relationship
        self.name = name
        self.proof_attrs = proof_attrs
        self.proof_predicates = proof_predicates
        self.created = thread_id is None

    def request_msg(self):
        msg = self._get_base_message(self.PROOF_REQUEST)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['name'] = self.name
        msg['proof_attrs'] = self.proof_attrs
        if self.proof_predicates:
            msg['proof_predicates'] = self.proof_predicates
        return msg

    async def request_msg_packed(self, context):
        return await self.get_message_bytes(context, self.request_msg())

    async def request(self, context):
        await self.send_message(context, await self.request_msg_packed(context))

    def status_msg(self):
        msg = self._get_base_message(self.STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, self.status_msg())

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))

    def reject_msg(self, reason):
        msg = self._get_base_message(self.REJECT)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['reason'] = reason
        return msg

    async def reject_msg_packed(self, context, reason):
        return await self.get_message_bytes(context, self.reject_msg(reason))

    async def reject(self, context, reason):
        await self.send_message(context, await self.reject_msg_packed(context, reason))
