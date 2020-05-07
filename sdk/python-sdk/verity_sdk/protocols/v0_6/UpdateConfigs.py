from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class UpdateConfigs(Protocol):
    MSG_FAMILY = 'update-configs'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    UPDATE_CONFIGS = 'update'
    GET_STATUS = 'get-status'

    def __init__(self, name=None, logo_url=None):
        super().__init__(self.MSG_FAMILY,
                         self.MSG_FAMILY_VERSION,
                         msg_qualifier=EVERNYM_MSG_QUALIFIER)
        self.name = name
        self.logo_url = logo_url

    def update_msg(self, context):
        msg = self._get_base_message(self.UPDATE_CONFIGS)
        msg['configs'] = [
            {'name': 'name', 'value': self.name},
            {'name': 'logoUrl', 'value': self.logo_url}
        ]

        return msg

    async def update_msg_packed(self, context):
        msg = self.update_msg(context)
        return await self.get_message_bytes(context, msg)

    async def update(self, context):
        return await self.send_message(context, await self.update_msg_packed(context))

    def status_msg(self, _):
        msg = self._get_base_message(self.GET_STATUS)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, self.status_msg(context))

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
