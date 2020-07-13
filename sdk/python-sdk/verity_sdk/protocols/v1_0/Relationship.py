from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class Relationship(Protocol):
    MSG_FAMILY = 'relationship'
    MSG_FAMILY_VERSION = '1.0'

    CREATED = 'created'
    INVITATION = 'invitation'
    CREATE = 'create'
    CONNECTION_INVITATION = 'connection-invitation'
    OUT_OF_BAND_INVITATION = 'out-of-band-invitation'

    def __init__(self,
                 for_relationship: str = None,
                 thread_id: str = None,
                 label: str = None,
                 goal_code: str = None,
                 goal: str = None,
                 request=None):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
            thread_id=thread_id
        )

        self.for_relationship = for_relationship
        if label:
            self.label = label
        else:
            self.label = ''
        self.goal_code = goal_code
        self.goal = goal
        self.request = request

    def create_msg(self, _):
        msg = self._get_base_message(self.CREATE)
        self._add_thread(msg)
        msg['label'] = self.label

        return msg

    async def create_msg_packed(self, context):
        return await self.get_message_bytes(context, self.create_msg(context))

    async def create(self, context):
        await self.send_message(context, await self.create_msg_packed(context))

    def connection_invitation_msg(self, _):
        msg = self._get_base_message(self.CONNECTION_INVITATION)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def connection_invitation_msg_packed(self, context):
        return await self.get_message_bytes(context, self.connection_invitation_msg(context))

    async def connection_invitation(self, context):
        await self.send_message(context, await self.connection_invitation_msg_packed(context))

    def out_of_band_invitation_msg(self, _):
        msg = self._get_base_message(self.OUT_OF_BAND_INVITATION)
        msg['goal_code'] = self.goal_code
        msg['goal'] = self.goal
        msg['request~attach'] = self.request
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def out_of_band_invitation_msg_packed(self, context):
        return await self.get_message_bytes(context, self.out_of_band_invitation_msg(context))

    async def out_of_band_invitation(self, context):
        await self.send_message(context, await self.out_of_band_invitation_msg_packed(context))
