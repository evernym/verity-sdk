from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class UpdateConfigs(Protocol):
    MSG_FAMILY = 'configs'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    UPDATE_CONFIGS = 'UPDATE_CONFIGS'

    def __init__(self, name, logo_url, thread_id=None):
        super().__init__(self.MSG_FAMILY,
                         self.MSG_FAMILY_VERSION,
                         msg_qualifier=EVERNYM_MSG_QUALIFIER,
                         thread_id=thread_id)
        self.name = name
        self.logo_url = logo_url

    def update_msg(self, context):
        return {
            '@type': {'name': 'UPDATE_CONFIGS', 'ver': '1.0'},
            'configs': [
                {'name': 'name', 'value': self.name},
                {'name': 'logoUrl', 'value': self.logo_url}
            ]
        }

    async def update_msg_packed(self, context):
        msg = self.update_msg(context)
        return await self.get_message_bytes(context, msg)

    async def update(self, context):
        return await self.send_message(context, await self.update_msg_packed(context))
