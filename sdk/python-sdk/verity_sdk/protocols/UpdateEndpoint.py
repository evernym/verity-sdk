from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import get_message_type, get_problem_report_message_type, get_status_message_type
from verity_sdk.utils.Context import Context


class UpdateEndpoint(Protocol):
    MSG_FAMILY = 'configs'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    UPDATE_ENDPOINT = 'UPDATE_COM_METHOD'

    def __init__(self, context: Context):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION)
        self.context: Context = context


    async def update_endpoint_msg(self, context):
        COM_METHOD_TYPE = 2
        if context.endpoint_url is None or context.endpoint_url == '':
            raise Exception('Unable to update endpoint because context.endpoint_url is not defined')
        msg = self._get_base_message(self.UPDATE_ENDPOINT)
        msg['comMethod'] = {
            'id': 'webhook',
            'type': COM_METHOD_TYPE,
            'value': self.context.endpoint_url
        }
        return msg

    async def update_endpoint_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.update_endpoint_msg(context))

    async def update_endpoint(self, context):
        await self.send_message(context, await self.update_endpoint_msg_packed(context))
