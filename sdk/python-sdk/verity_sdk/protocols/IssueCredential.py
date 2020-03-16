from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class IssueCredential(Protocol):
    MSG_FAMILY = 'issue-credential'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    OFFER_CREDENTIAL = 'send-offer'
    ISSUE_CREDENTIAL = 'issue-credential'
    GET_STATUS = 'get-status'
    ASK_ACCEPT = 'ask-accept'

    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 name: str = None,
                 cred_def_id: str = None,
                 credential_values: dict = None,
                 price: str = '0'):
        super().__init__(self.MSG_FAMILY,
                         self.MSG_FAMILY_VERSION,
                         msg_qualifier=EVERNYM_MSG_QUALIFIER,
                         thread_id=thread_id)
        self.for_relationship: str = for_relationship
        self.name: str = name
        self.cred_def_id: str = cred_def_id
        self.credential_values: dict = credential_values
        self.price: str = price


    async def offer_credential_msg(self, _):
        msg = self._get_base_message(self.OFFER_CREDENTIAL)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['name'] = self.name
        msg['credDefId'] = self.cred_def_id
        msg['credentialValues'] = self.credential_values
        msg['price'] = self.price
        return msg

    async def offer_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.offer_credential_msg(context))

    async def offer_credential(self, context):
        await self.send_message(context, await self.offer_credential_msg_packed(context))

    async def issue_credential_msg(self, _):
        msg = self._get_base_message(self.ISSUE_CREDENTIAL)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def issue_credential_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.issue_credential_msg(context))

    async def issue_credential(self, context):
        await self.send_message(context, await self.issue_credential_msg_packed(context))

    async def status_msg(self, _):
        msg = self._get_base_message(self.GET_STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.status_msg(context))

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
