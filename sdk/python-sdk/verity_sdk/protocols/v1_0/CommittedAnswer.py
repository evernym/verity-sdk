from typing import List

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER


class CommittedAnswer(Protocol):
    """
    The CommittedAnswer protocol allows one self-sovereign party ask another self-sovereign a question and get a
    answer. The answer can be signed (not required) and must be one of a specified list of responses
    """

    MSG_FAMILY = 'committedanswer'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    # Messages
    ASK_QUESTION = 'ask-question'
    """Name for 'ask-question' control message"""
    GET_STATUS = 'get-status'
    """Name for 'get-status' control message"""
    ANSWER_QUESTION = 'answer-question'
    """Name for 'answer-question' signal message"""
    ANSWER_GIVEN = 'answer-given'
    """Name for 'answer-given' signal message"""

    # pylint:disable=too-many-arguments
    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 question: str = None,
                 descr: str = '',
                 valid_responses: List[str] = None,
                 signature_required: bool = True,
                 answer_str: str = None,
                 msg_family: str = MSG_FAMILY,  # Used to override the msg_family for the
                 msg_family_version: str = MSG_FAMILY_VERSION,
                 msg_qualifier: str = COMMUNITY_MSG_QUALIFIER):
        """
        Args:
            for_relationship (str): the relationship identifier (DID) for the pairwise relationship that will be used
            thread_id (str): the thread id of the already started protocol
            question (str): The main text of the question (included in the message the Connect.Me user signs with their private key)
            descr (str): Any additional information about the question
            valid_responses (List[str]): The given possible responses.
            signature_required (bool): if a signature must be included with the answer
            answer_str (str): The given answer from the list of valid responses given in the question
        """
        super().__init__(msg_family, msg_family_version, msg_qualifier, thread_id)

        self.for_relationship: str = for_relationship
        self.question: str = question
        self.descr: str = descr
        self.valid_responses: List[str] = valid_responses
        self.signature_required: bool = signature_required
        self.answer_str: str = answer_str

    async def ask(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.ask_msg_packed(context))

    def ask_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.ASK_QUESTION)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['text'] = self.question
        msg['detail'] = self.descr
        msg['valid_responses'] = self.valid_responses or []
        msg['signature_required'] = self.signature_required
        return msg

    async def ask_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.ask_msg(context))

    async def answer(self, context):
        """
        Directs verity-application to answer the question

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.answer_msg_packed(context))

    def answer_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.ANSWER_QUESTION)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg.response = self.answer_str
        return msg

    async def answer_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.answer_msg(context))

    async def status(self, context):
        """
        Ask for status from the verity-application agent

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
        msg = self._get_base_message(self.GET_STATUS)
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
