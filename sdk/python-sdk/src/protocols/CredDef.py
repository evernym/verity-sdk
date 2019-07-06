from src.protocols.Protocol import Protocol

class CredDef(Protocol):

  # Message Type Definitions
  WRITE_CRED_DEF_MESSAGE_TYPE = "vs.service/cred-def/0.1/write"
  PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/cred-def/0.1/problem-report"
  STATUS_MESSAGE_TYPE = "vs.service/cred-def/0.1/status"

  # Status Definitions
  WRITE_SUCCESSFUL_STATUS = 0

  name: str
  schemaId: str
