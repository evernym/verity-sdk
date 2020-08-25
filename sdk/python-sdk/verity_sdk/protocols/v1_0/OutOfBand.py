from decimal import Context

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class OutOfBand(Protocol):
    """
    The OutOfBand protocol allow an interaction intent, connection information and reuse of a relationship.
    """

    MSG_FAMILY = 'out-of-band'
    """the family name for the message family"""
    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    REUSE = 'reuse'
    """Name for 'reuse' control message"""

    def __init__(self,
                 for_relationship: str = None,
                 invite_url: str = None):
        """
        Args:
            for_relationship (str): the relationship identifier (DID) for the pairwise relationship that will be reused
            invite_url (str): the Out-of-Band invitation url
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER
        )
        self.for_relationship = for_relationship
        self.invite_url = invite_url

    async def reuse(self, context: Context):
        """
        Direct the verity-application agent to reuse the relationship given.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.reuse_msg_packed(context))

    def reuse_msg(self, context: Context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.REUSE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)

        msg['inviteUrl'] = self.invite_url
        return msg

    async def reuse_msg_packed(self, context: Context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.reuse_msg(context))
