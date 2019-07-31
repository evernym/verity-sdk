from typing import Callable, List, Optional, Coroutine, Any

from verity_sdk.utils import Context, unpack_message


def is_problem_report(message_type: str) -> bool:
  return message_type.split('/')[3] == 'problem-report'


class Handler():
  message_type: str
  message_status: Optional[int]
  message_handler: Callable[[dict], Coroutine[Any, Any, None]]

  def __init__(
      self,
      message_type: str,
      message_status: Optional[int],
      message_handler: Callable[[dict], Coroutine[Any, Any, None]]):
    self.message_type = message_type
    self.message_status = message_status
    self.message_handler = message_handler

  def handles(self, message: dict) -> bool:
    if self.message_status is not None:
      return message['@type'] == self.message_type and message['status'] == self.message_status
    return message['@type'] == self.message_type

  async def handle(self, message: dict):
    await self.message_handler(message)

  async def __call__(self, message: dict):
    await self.message_handler(message)


class Handlers():
  handlers: List[Handler]
  default_handler: Callable[[dict], Coroutine[Any, Any, None]]
  problem_report_handler: Callable[[dict], Coroutine[Any, Any, None]]

  def __init__(self):
    self.handlers = []

  def add_handler(
      self,
      message_type: str,
      message_status: int,
      message_handler: Callable[[dict], Coroutine[Any, Any, None]]):
    self.handlers.append(Handler(message_type, message_status, message_handler))

  def add_default_handler(self, handler: Callable[[dict], Coroutine[Any, Any, None]]):
    self.default_handler = handler

  def add_problem_report_handler(self, handler: Callable[[dict], Coroutine[Any, Any, None]]):
    self.problem_report_handler = handler

  async def handle_message(self, context: Context, raw_message: bytes):
    message: dict = await unpack_message(context, raw_message)
    handled: bool = False

    for handler in self.handlers:
      if handler.handles(message):
        await handler(message)
        handled = True

    if not handled:
      if is_problem_report(message['@type']) and self.problem_report_handler is not None:
        await self.problem_report_handler(message)
      elif self.default_handler is not None:
        await self.default_handler(message)


# Enables handler registration decorator @AddHandler(handlers, message_type, message_status)
class AddHandler():

  def __init__(self, handlers: Handlers, message_type: str, message_status: int):
    self.handlers = handlers
    self.message_type = message_type
    self.message_status = message_status

  def __call__(self, handler: Callable[[dict], Coroutine[Any, Any, None]], *args, **kwargs):
    self.handlers.add_handler(self.message_type, self.message_status, handler)
