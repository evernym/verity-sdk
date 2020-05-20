from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class UpdateEndpoint(Protocol):
    MSG_FAMILY = 'configs'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    UPDATE_ENDPOINT = 'UPDATE_COM_METHOD'

    def __init__(self):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )

    def update_endpoint_msg(self, context):
        COM_METHOD_TYPE = 2
        if context.endpoint_url is None or context.endpoint_url == '':
            raise Exception('Unable to update endpoint because context.endpoint_url is not defined')
        msg = self._get_base_message(self.UPDATE_ENDPOINT)
        msg['comMethod'] = {
            'id': 'webhook',
            'type': COM_METHOD_TYPE,
            'value': context.endpoint_url,
            'packaging': {
                'pkgType': '1.0',
                'recipientKeys': [context.sdk_verkey]
            }
        }
        return msg

    async def update_endpoint_msg_packed(self, context):
        msg = self.update_endpoint_msg(context)
        return await self.get_message_bytes(context, msg)

    async def update(self, context):
        bytes_to_send = await self.update_endpoint_msg_packed(context)
        await self.send_message(context, bytes_to_send)
