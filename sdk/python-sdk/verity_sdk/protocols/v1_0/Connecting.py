from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class Connecting(Protocol):
    """
    The Connecting protocols form secure, private and self-sovereign channel between two independent parties.
    This protocol facilitates the exchange of keys and endpoints that will be used in all future interactions.
    This connecting process starts with the Relationship protocol which provisions a relationship on the Verity
    Application and creates the an invite. With the invite, this Connecting protocol can be started and completed.
    """

    MSG_FAMILY = 'connections'
    """the family name for the message family"""
    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    REQUEST_RECEIVED = 'request-received'
    """Name for 'request-received' signal message"""
    RESPONSE_SENT = 'response-sent'
    """Name for 'response-sent' signal message"""
    ACCEPT_INVITE = 'accept'
    """Name for 'accept' control message"""
    STATUS = 'status'
    """Name for 'status' control message"""

    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 label: str = None,
                 base64_invite_url: str = None):
        """
        Args:
            for_relationship (str): The relationship identifier (DID) to use for the connections exchange. Normally,
                its relationship will have been created as a reaction to receiving the invitation.
            thread_id (str): The thread id of the already started protocol
            label (str): A human readable string that will label the caller identity (often an organization).
                E.g. 'Acme Corp`
            base64_invite_url (str): the invitation URL as specified by the Aries 0160: Connection Protocol
                (eg. https://<domain>/<path>?c_i=<invitation-string>)
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )

        self.for_relationship = for_relationship
        self.label = label
        self.base64_invite_url = base64_invite_url

    async def accept(self, context):
        """
        Accepts connection defined by the given invitation

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.accept_msg_packed(context))

    def accept_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.ACCEPT_INVITE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['label'] = self.label
        msg['invite_url'] = self.base64_invite_url
        return msg

    async def accept_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.accept_msg(context))

    async def status(self, context):
        """
        Sends the get status message to the connection

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.status_msg_packed(context))

    def status_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
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
