from verity_sdk.transports import send_packed_message
from verity_sdk.utils import pack_message_for_verity, uuid
from verity_sdk.utils.MessageFamily import MessageFamily


class Protocol(MessageFamily):
    """The base class for all protocols"""

    # Messages
    STATUS = 'status-report'
    """Name for 'status-report' signal message"""
    PROBLEM_REPORT = 'problem-report'
    """Name for 'problem-report' signal message"""

    def __init__(self, msg_family: str, msg_family_version: str, msg_qualifier: str = None, thread_id: str = None):
        """
        Args:
            msg_family (str): the family name for the message family
            msg_family_version (str): the version for the message family
            msg_qualifier (str): the qualifier for the message family
            thread_id (str): given ID used for the thread.
        """
        super().__init__(msg_family, msg_family_version, msg_qualifier)
        if thread_id is not None:
            self.thread_id = thread_id
        else:
            self.thread_id = uuid()

    def _add_thread(self, msg):
        msg['~thread'] = {
            'thid': self.thread_id
        }

    @staticmethod
    def _add_relationship(msg, for_relationship):
        msg['~for_relationship'] = for_relationship

    @staticmethod
    async def get_message_bytes(context, message) -> bytes:
        """
        Packs the connection message for the verity
        Args:
            context (Context): an instance of Context that has been initialized with your wallet and key details
            message (dict): the message to be packed for the verity-application
        Returns:
            bytes: Encrypted connection message ready to be sent to the verity
        """
        return await pack_message_for_verity(context, message)

    @staticmethod
    async def send_message(context, packed_message):
        """
        Sends a given packed message to Verity

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
            packed_message (bytes):  the encrypted message bytes to send to Verity
        """
        send_packed_message(context, packed_message)
