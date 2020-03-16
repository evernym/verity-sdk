from verity_sdk.utils import uuid
from verity_sdk.protocols.Protocol import Protocol


class Connecting(Protocol):
    MSG_FAMILY = 'connecting'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    CREATE_CONNECTION = 'CREATE_CONNECTION'
    INVITE_DETAIL = 'CONN_REQUEST_RESP'
    CONN_REQ_ACCEPTED = 'CONN_REQ_ACCEPTED'
    GET_STATUS = 'get-status'

    def __init__(self, source_id: str = uuid(), phone_number: str = None, include_public_did: bool = False):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION,
                         thread_id=source_id)  # Connecting 0.6 uses the source_id as the thread_id. Should be resolved in 1.0.
        self.source_id = source_id
        self.phone_number = phone_number
        self.include_public_did = include_public_did

    async def connect_msg(self, _):
        msg = self._get_base_message(self.CREATE_CONNECTION)
        msg['sourceId'] = self.source_id
        msg['phoneNo'] = self.phone_number
        msg['includePublicDID'] = self.include_public_did
        return msg

    async def connect_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.connect_msg(context))

    async def connect(self, context):
        return await self.send_message(context, await self.connect_msg_packed(context))

    async def status_msg(self, _):
        msg = self._get_base_message(self.GET_STATUS)
        msg['sourceId'] = self.source_id
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.status_msg(context))

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
