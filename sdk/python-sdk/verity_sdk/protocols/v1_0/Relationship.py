from enum import Enum

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class GoalsList(Enum):
    class GoalCode:
        def __init__(self, code, name) -> None:
            self.code = code
            self.name = name

    ISSUE_VC = GoalCode('issue-vc', 'To issue a credential')
    REQUEST_PROOF = GoalCode('request-proof', 'To request a proof')
    CREATE_ACCOUNT = GoalCode('create-account', 'To create an account with a service')
    P2P_MESSAGING = GoalCode('p2p-messaging', 'To establish a peer-to-peer messaging relationship')


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
                 logo_url: str = None):
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
        self.goal = GoalsList.P2P_MESSAGING
        self.logo_url = logo_url

    def create_msg(self, _):
        msg = self._get_base_message(self.CREATE)
        self._add_thread(msg)
        msg['label'] = self.label
        if self.logo_url:
            msg['logoUrl'] = self.logo_url

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
        msg['goalCode'] = self.goal.value.code
        msg['goal'] = self.goal.value.name
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def out_of_band_invitation_msg_packed(self, context):
        return await self.get_message_bytes(context, self.out_of_band_invitation_msg(context))

    async def out_of_band_invitation(self, context):
        await self.send_message(context, await self.out_of_band_invitation_msg_packed(context))
