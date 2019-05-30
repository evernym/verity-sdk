from src.handlers import is_problem_report

def test_is_problem_report():
  assert is_problem_report('vs.service/enroll/0.1/problem-report')
  assert not is_problem_report('vs.service/enroll/0.1/enroll')
