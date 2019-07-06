from typing import List
from uuid import uuid4 as uuid

from src.utils import Context
from src.protocols.Protocol import Protocol

class Schema(Protocol):

  # Message Type Definitions
  WRITE_SCHEMA_MESSAGE_TYPE = "vs.service/schema/0.1/write"
  PROBLEM_REPORT_MESSAGE_TYPE = "vs.service/schema/0.1/problem-report"
  STATUS_MESSAGE_TYPE = "vs.service/schema/0.1/status"

  # Status Definitions
  WRITE_SUCCESSFUL_STATUS = 0

  name: str
  version: str
  attrs: List[str]
  messages = {
    WRITE_SCHEMA_MESSAGE_TYPE: { 
      '@type': WRITE_SCHEMA_MESSAGE_TYPE,
      '@id': uuid(),
      'schema': {
        'name': name,
        'version': version,
        'attrNames': attrs
      }
    }
  }

  def __init__(self, name: str, version: str, *attrs: str):
    super(Schema, self).__init__()
    self.name = name
    self.version = version
    self.attrs = attrs

  def write(self, context: Context):
    self.send(context, self.messages[Schema.WRITE_SCHEMA_MESSAGE_TYPE])
