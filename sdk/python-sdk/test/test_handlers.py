from src.handlers import is_problem_report, Handlers, AddHandler


def test_is_problem_report():
  assert is_problem_report('vs.service/enroll/0.1/problem-report')
  assert not is_problem_report('vs.service/enroll/0.1/enroll')


def test_decorator():
  handlers = Handlers()

  @AddHandler(handlers, "some type", 0)
  def some_handler(msg: dict) -> None:
    print("do something")

  @AddHandler(handlers, "another type", 1)
  def another_handler(msg: dict) -> None:
    print("do another thing")

  assert len(handlers.handlers) == 2
  assert handlers.handlers[0].message_type == "some type"
  assert handlers.handlers[0].message_status == 0
  handlers.handlers[0]("message")
  assert handlers.handlers[1].message_type == "another type"
  assert handlers.handlers[1].message_status == 1
  handlers.handlers[1]("message")

