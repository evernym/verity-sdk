from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class UpdateEndpoint(Protocol):
    """ The UpdateEndpoint protocol allow changes to the endpoint register with the verity-application agent.
    This endpoint is where agent sends signal messages. These messages are asynchronous and are sent via a http
    web hook. The endpoint is defined in the context. It must be set in the context before using this protocol.
    """

    MSG_FAMILY = 'configs'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '0.6'
    """the version for the message family"""

    # Messages
    UPDATE_ENDPOINT = 'UPDATE_COM_METHOD'
    """Name for 'update' control message"""

    def __init__(self):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )

    async def update(self, context):
        """
        Directs verity-application to update the used endpoint for out-going signal message to the endpoint contained
        in the context object.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        bytes_to_send = await self.update_endpoint_msg_packed(context)
        await self.send_message(context, bytes_to_send)

    def update_endpoint_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """

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
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """

        msg = self.update_endpoint_msg(context)
        return await self.get_message_bytes(context, msg)
