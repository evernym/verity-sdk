import pytest

from verity_sdk.handlers import is_problem_report, Handlers, AddHandler


def test_is_problem_report():
  assert is_problem_report('vs.service/enroll/0.1/problem-report')
  assert not is_problem_report('vs.service/enroll/0.1/enroll')

#pylint: disable=unused-variable
@pytest.mark.asyncio
async def test_decorator():
  handlers = Handlers()

  @AddHandler(handlers, 'some type', 0)
  async def some_handler(msg: dict) -> None:
    print('do something with {}'.format(msg))

  @AddHandler(handlers, 'another type', 1)
  async def another_handler(msg: dict) -> None:
    print('do another thing with {}'.format(msg))

  assert len(handlers.handlers) == 2
  assert handlers.handlers[0].message_type == 'some type'
  assert handlers.handlers[0].message_status == 0
  await handlers.handlers[0]({})
  assert handlers.handlers[1].message_type == 'another type'
  assert handlers.handlers[1].message_status == 1
  await handlers.handlers[1]({})
