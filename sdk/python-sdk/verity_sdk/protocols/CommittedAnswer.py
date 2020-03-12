from typing import List

from verity_sdk.protocols.Protocol import Protocol

class CommittedAnswer(Protocol):
    MSG_FAMILY = 'committedanswer'
    MSG_FAMILY_VERSION = '1.0'

    # Messages
    ASK_QUESTION = 'ask-question'
    GET_STATUS = 'get-status'
    ANSWER_QUESTION = 'answer-question'
    ANSWER_GIVEN = 'answer-given'

    #pylint:disable=too-many-arguments
    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 question: str = None,
                 descr: str = '',
                 valid_responses: List[str] = None,
                 signature_required: bool = True,
                 answer_str=None,
                 msg_family: str = MSG_FAMILY,  # Used to override the msg_family for the
                 msg_family_version: str = MSG_FAMILY_VERSION,
                 msg_qualifier: str = None):
        super().__init__(msg_family, msg_family_version, msg_qualifier, thread_id)

        self.for_relationship: str = for_relationship
        self.question: str = question
        self.descr: str = descr
        self.valid_responses: List[str] = valid_responses
        self.signature_required: bool = signature_required
        self.answer_str: str = answer_str

    async def ask_msg(self, _):
        msg = self._get_base_message(self.ASK_QUESTION)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['text'] = self.question
        msg['detail'] = self.descr
        msg['valid_responses'] = self.valid_responses or []
        msg['signature_required'] = self.signature_required
        return msg

    async def ask_msg_packed(self, context):
        return self.get_message_bytes(context, await self.ask_msg(context))

    async def ask(self, context):
        await self.send_message(context, await self.ask_msg_packed(context))

    async def answer_msg(self, _):
        msg = self._get_base_message(self.ANSWER_QUESTION)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg.response = self.answer_str
        return msg

    async def answer_msg_packed(self, context):
        return self.get_message_bytes(context, await self.answer_msg(context))

    async def answer(self, context):
        await self.send_message(context, await self.answer_msg_packed(context))

    async def status_msg(self, _):
        msg = self._get_base_message(self.GET_STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.status_msg(context))

    async def status(self, context):
        await self.send_message(context, await self.status_msg_packed(context))
