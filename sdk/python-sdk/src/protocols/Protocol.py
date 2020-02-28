import abc

from src.utils import Context, pack_message_for_verity, uuid
from src.transports import send_message


class Protocol:
    MSG_FAMILY = 'none'
    MSG_FAMILY_VERSION = '0.0.0'

    messages: dict
    thread_id: str

    def __init__(self, thread_id: str = None):
        if thread_id:
            self.thread_id = thread_id
        else:
            self.thread_id = uuid()

    @staticmethod
    async def get_message(context: Context, message: dict) -> bytes:
        return await pack_message_for_verity(context, message)

    @staticmethod
    async def send(context: Context, message: dict) -> bytes:
        message = await Protocol.get_message(context, message)
        send_message(context.verity_url, message)
        return message

    @staticmethod
    def get_new_id():
        return uuid()

    @abc.abstractmethod
    def define_messages(self):
        pass

    def get_thread_block(self) -> dict:
        return {
            'thid': self.thread_id
        }
