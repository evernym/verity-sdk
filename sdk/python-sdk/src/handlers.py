def is_problem_report(message_type: str) -> bool:
  return message_type.split('/')[3] == 'problem-report'
