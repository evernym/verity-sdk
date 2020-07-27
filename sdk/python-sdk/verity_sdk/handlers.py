import logging
from typing import Callable, NewType, Awaitable, Dict

from verity_sdk.utils import Context, unpack_message, MsgType

HandlerFunction = NewType('HandlerFunction', Callable[[str, dict], Awaitable[None]])
DefaultHandlerFunction = NewType('DefaultHandlerFunction', Callable[[dict], Awaitable[None]])


class Handler:
    """
    Defines how to handle a message of a certain type and optionally with a particular status
    """
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
        msg_type = MsgType(message['@type'])
        return msg_type.msg_family == self.msg_family and msg_type.msg_family_version == self.msg_family_version

    async def handle(self, msg_name: str, message: dict):
        """
        Handles the given JSON object message

        Args:
         message (dict): the message to be handled
        """
        await self.handler_function(msg_name, message)

    async def __call__(self, msg_name: str, message: dict):  # Added for clarity and flexibility. Same as `self.handle`.
        await self.handler_function(msg_name, message)


class Handlers:
    """
    Stores an array of message handlers that are used when receiving an inbound message
    """
    _handlers: Dict[str, Handler]
    _default_handler: DefaultHandlerFunction

    def __init__(self):
        self._handlers = {}

    def add_handler(
            self,
            msg_family: str,
            msg_family_version: str,
            handler_function: HandlerFunction):
        """
        Adds a MessageHandler for a message type to the list if current message handlers

        Args:
            msg_family (str): the family of the message to be handled
        msg_family_version (str): the family version to be handled
        handler_function (HandlerFunction):  the handler function itself
        """
        key = Handlers._build_handlers_key(msg_family, msg_family_version)
        self._handlers[key] = Handler(msg_family, msg_family_version, handler_function)

    def has_handler(self, msg_family: str, msg_family_version: str):
        """
        checks list of handlers to see if a handler is register for family

        msg_family (str): the family of the message to be checked
        msg_family_version (str): the family version to be checked

        Returns:
            bool: if there is a handler for given parameters
        """
        return Handlers._build_handlers_key(msg_family, msg_family_version) in self._handlers

    def set_default_handler(self, handler_function: DefaultHandlerFunction):
        """
        Sets a handler to be called for messages where there is no registered handler
        Args:
            handler_function (DefaultHandlerFunction): the function that will be called
        """
        self._default_handler = handler_function

    async def handle_message(self, context: Context, raw_message: bytes):
        """
        Calls all of the handlers that support handling of this particular message type and message status

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
            raw_message (bytes): the raw bytes received from Verity
        """
        message: dict = await unpack_message(context, raw_message)
        msg_type = MsgType(message['@type'])

        if self.has_handler(msg_type.msg_family, msg_type.msg_family_version):
            key = Handlers._build_handlers_key(msg_type.msg_family, msg_type.msg_family_version)
            await self._handlers[key].handle(msg_type.msg_name, message)
        else:
            if self._default_handler is not None:
                await self._default_handler(message)
            else:
                logging.warning('Unable to handle message, and no default handler was defined')

    @staticmethod
    def _build_handlers_key(msg_family: str, msg_family_version: str):
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
