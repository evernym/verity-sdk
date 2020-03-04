import logging
from typing import Callable, List, Any, NewType, Awaitable, Dict

from src.utils import Context, unpack_message, MsgType


def is_problem_report(message_type: str) -> bool:
    return message_type.split('/')[3] == 'problem-report'

HandlerFunction = NewType('HandlerFunction', Callable[[str, dict], Awaitable[None]])

class Handler:

    def __init__(
            self,
            msg_family: str,
            msg_family_version: str,
            handler_function: HandlerFunction):
        self.msg_family: str = msg_family
        self.msg_family_version: str = msg_family_version
        self.handler_function: HandlerFunction = handler_function

    def handles(self, message: dict) -> bool:
        if '@type' not in message:
            logging.error('message does not contain an "@type" attribute')
            return False
        type = MsgType(message['@type'])
        return type.msg_family == self.msg_family and type.msg_family_version == self.msg_family_version

    async def handle(self, msg_name: str, message: dict):
        await self.handler_function(msg_name, message)

    async def __call__(self, msg_name: str, message: dict): # Added for clarity and flexibility. Same as `self.handle`.
        await self.handler_function(msg_name, message)


class Handlers:
    handlers: Dict[str, Handler]
    default_handler: HandlerFunction

    def __init__(self):
        self.handlers = {}

    def add_handler(
            self,
            msg_family: str,
            msg_family_version: str,
            handler_function: HandlerFunction):
        self.handlers[Handlers.build_handlers_key(msg_family, msg_family_version)] = Handler(msg_family, msg_family_version, handler_function)

    def has_handler(self, msg_family: str, msg_family_version: str):
        return Handlers.build_handlers_key(msg_family, msg_family_version) in self.handlers

    def set_default_handler(self, handler_function: HandlerFunction):
        self.default_handler = handler_function

    async def handle_message(self, context: Context, raw_message: bytes):
        message: dict = await unpack_message(context, raw_message)
        msg_type = MsgType(message['@type'])

        if self.has_handler(msg_type.msg_family, msg_type.msg_family_version):
            await self.handlers[Handlers.build_handlers_key(msg_type.msg_family, msg_type.msg_family_version)].handle(msg_type.msg_name, message)
        else:
            if self.default_handler is not None:
                await self.default_handler(msg_type.msg_name, message)
            else:
                logging.warning("Unable to handle message, and no default handler was defined")

    @staticmethod
    def build_handlers_key(msg_family: str, msg_family_version: str):
        return '{}{}'.format(msg_family, msg_family_version)


# # Enables handler registration decorator @AddHandler(handlers, msg_family, msg_family_version)
# class AddHandler:
#
#     def __init__(self, handlers: Handlers, msg_family: str, msg_family_version: str,):
#         self.handlers = handlers
#         self.msg_family = msg_family
#         self.msg_family_version = msg_family_version
#
#     def __call__(self, handler_function: HandlerFunction, *args, **kwargs):
#         self.handlers.add_handler(self.msg_family, self.msg_family_version, handler_function)
