from verity_sdk.protocols.Protocol import Protocol

class UpdateConfigs(Protocol):

    # Messages
    UPDATE_CONFIGS = 'UPDATE_CONFIGS'

    def __init__(self, name, logo_url, thread_id = None):
        super().__init__(thread_id)
        self.name = name
        self.logo_url = logo_url

    async def update_msg(self):
        return {
            '@type': { 'name': 'UPDATE_CONFIGS', 'ver': '1.0' },
            'configs': [
                { 'name': 'name', 'value': self.name },
                { 'name': 'logoUrl', 'value': self.logo_url }
            ]
        }

    async def update_msg_packed(self, context):
        return await self.get_message(context, await self.update_msg())

    async def update(self, context):
        return await self.send(context, await self.update_msg())

    # TODO: Fix the base class to follow the new format
    def define_messages(self):
        pass
