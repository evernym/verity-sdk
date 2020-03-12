from typing import List

from verity_sdk.protocols.Protocol import Protocol

class PresentProof(Protocol):
    MSG_FAMILY = 'present-proof'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    PROOF_REQUEST = 'request'
    GET_STATUS = 'get-status'
    PROOF_RESULT = 'proof-result'

    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 name: str = None,
                 proof_attrs: List[dict] = None,
                 proof_predicates: List[dict] = None,
                 revocation_interval: dict = None):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION, thread_id=thread_id)
        self.for_relationship: str = for_relationship
        self.name: str = name
        self.proof_attrs: List[dict] = proof_attrs
        self.proof_predicates: List[dict] = proof_predicates
        self.revocation_interval: dict = revocation_interval or {}

    async def request_msg(self, _):
        msg = self._get_base_message(self.PROOF_REQUEST)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['name'] = self.name
        msg['proofAttrs'] = self.proof_attrs
        if self.proof_predicates is not None:
            msg['proofPredicates'] = self.proof_predicates
        return msg

    async def request_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.request_msg(context))

    async def request(self, context):
        await self.send_message(context, await self.request_msg_packed(context))

    async def status_msg(self, _):
        msg = self._get_base_message(self.GET_STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.status_msg(context))

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
