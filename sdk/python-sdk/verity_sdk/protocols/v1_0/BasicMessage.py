from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class BasicMessage(Protocol):
    """
    The BasicMessage protocol allows one self-sovereign party send another self-sovereign a message.
    """

    MSG_FAMILY = 'basicmessage'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    # Messages
    BASIC_MESSAGE = 'send-message'
    """Name for 'send-message' control message"""

    # pylint:disable=too-many-arguments
    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 content: str = None,
                 sent_time: str = '',
                 localization: str = None):
        """
        Args:
            for_relationship (str): the relationship identifier (DID) for the pairwise relationship that will be used
            thread_id (str): the thread id of the already started protocol
            content (str): The main text of the message
            sent_time (str): The time the message was sent
            localization (str): Language localization code
        """
        super().__init__(self.MSG_FAMILY,
                         self.MSG_FAMILY_VERSION,
                         msg_qualifier=COMMUNITY_MSG_QUALIFIER,
                         thread_id=thread_id)

        self.for_relationship: str = for_relationship
        self.content: str = content
        self.sent_time: str = sent_time
        self.localization: str = localization

    async def message(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.ask_msg_packed(context))

    def message_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.BASIC_MESSAGE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['content'] = self.content
        msg['sent_time'] = self.sent_time
        msg['~l10n'] = {'locale': self.localization}
        return msg

    async def message_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.ask_msg(context))
