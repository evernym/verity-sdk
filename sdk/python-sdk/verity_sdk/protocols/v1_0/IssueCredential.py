from typing import Dict

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Exeptions import WrongSetupException


class IssueCredential(Protocol):
    MSG_FAMILY = 'issue-credential'
    MSG_FAMILY_VERSION = '1.0'

    PROPOSE = 'propose'
    OFFER = 'offer'
    REQUEST = 'request'
    ISSUE = 'issue'
    REJECT = 'reject'
    STATUS = 'status'

    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 cred_def_id: str = None,
                 values: Dict[str, str] = None,
                 comment: str = None,
                 price: int = 0):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )

        self.created = thread_id is None
        self.for_relationship = for_relationship
        self.cred_def_id = cred_def_id
        self.values = values
        self.comment = comment
        self.price = price

    def propose_credential_msg(self):
        if not self.created:
            raise WrongSetupException('Unable to propose credentials when NOT starting the interaction')

        msg = self._get_base_message(self.PROPOSE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['credential_values'] = self.values
        msg['comment'] = self.comment
        return msg

    async def propose_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, self.propose_credential_msg())

    async def propose_credential(self, context):
        await self.send_message(context, await self.propose_credential_msg_packed(context))

    def offer_credential_msg(self):
        if not self.created:
            raise WrongSetupException('Unable to offer credentials when NOT starting the interaction')

        msg = self._get_base_message(self.OFFER)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['credential_values'] = self.values
        msg['comment'] = self.comment
        msg['price'] = self.price
        return msg

    async def offer_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, self.offer_credential_msg())

    async def offer_credential(self, context):
        await self.send_message(context, await self.offer_credential_msg_packed(context))

    def request_credential_msg(self):
        if not self.created:
            raise WrongSetupException('Unable to request credential when NOT starting the interaction')

        msg = self._get_base_message(self.REQUEST)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['comment'] = self.comment
        return msg

    async def request_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, self.request_credential_msg())

    async def request_credential(self, context):
        await self.send_message(context, await self.request_credential_msg_packed(context))

    def issue_credential_msg(self):
        msg = self._get_base_message(self.ISSUE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['comment'] = self.comment
        return msg

    async def issue_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, self.issue_credential_msg())

    async def issue_credential(self, context):
        await self.send_message(context, await self.issue_credential_msg_packed(context))

    def reject_msg(self):
        if not self.created:
            raise WrongSetupException('Unable to reject when NOT starting the interaction')

        msg = self._get_base_message(self.REJECT)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['comment'] = self.comment
        return msg

    async def reject_msg_packed(self, context):
        return await self.get_message_bytes(context, self.reject_msg())

    async def reject(self, context):
        await self.send_message(context, await self.reject_msg_packed(context))

    def status_msg(self):
        msg = self._get_base_message(self.STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, self.status_msg())

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
