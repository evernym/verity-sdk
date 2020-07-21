from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class UpdateConfigs(Protocol):
    """
    The UpdateConfigs protocol allow changes to the configuration of the registered agent on the verity-application.
    """

    MSG_FAMILY = 'update-configs'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '0.6'
    """the version for the message family"""

    # Messages
    UPDATE_CONFIGS = 'update'
    """Name for 'update' control message"""

    GET_STATUS = 'get-status'
    """Name for 'get-status' control message"""

    def __init__(self, name=None, logo_url=None):
        """
        Args:
            name (str): Organization's name that is presented to third-parties
            logo_url (str): A url to a logo that is presented to third-parties
        """
        super().__init__(self.MSG_FAMILY,
                         self.MSG_FAMILY_VERSION,
                         msg_qualifier=EVERNYM_MSG_QUALIFIER)
        self.name = name
        self.logo_url = logo_url

    async def update(self, context):
        """
        Directs verity-application to update the configuration register with the verity-application

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        return await self.send_message(context, await self.update_msg_packed(context))

    def update_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.UPDATE_CONFIGS)
        msg['configs'] = [
            {'name': 'name', 'value': self.name},
            {'name': 'logoUrl', 'value': self.logo_url}
        ]

        return msg

    async def update_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        msg = self.update_msg(context)
        return await self.get_message_bytes(context, msg)

    async def status(self, context):
        """
        Ask for status from the verity-application agent

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.status_msg_packed(context))

    def status_msg(self, _):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.GET_STATUS)
        return msg

    async def status_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.status_msg(context))
