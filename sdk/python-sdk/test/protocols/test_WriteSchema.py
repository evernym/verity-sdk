import pytest

from src.utils import unpack_forward_message
from src.utils.Context import Context
from src.protocols.WriteSchema import WriteSchema
from ..test_utils import get_test_config, send_stub, cleanup

schema_name = "schema name"
schema_version = "0.1.0"
attrs = ("name", "degree", "age")

def test_init():
  writeSchema = WriteSchema(schema_name, schema_version, attrs[0], attrs[1], attrs[2])

  assert writeSchema.name == schema_name
  assert writeSchema.version == schema_version
  assert len(writeSchema.attrs) == 3
  assert writeSchema.attrs == list(attrs)


@pytest.mark.asyncio
async def test_write():
  context = await Context.create(await get_test_config())
  writeSchema = WriteSchema(schema_name, schema_version, attrs[0], attrs[1], attrs[2])
  writeSchema.send = send_stub
  msg = await writeSchema.write(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == "{};spec/schema/0.1/write".format(WriteSchema.MESSAGE_TYPE_DID)
  assert msg['@id']
  assert msg['schema']['name'] == schema_name
  assert msg['schema']['version'] == schema_version
  assert msg['schema']['attrNames'] == list(attrs)

  await cleanup(context)


def test_get_message_type():
  writeSchema = WriteSchema(schema_name, schema_version, attrs[0], attrs[1], attrs[2])
  assert writeSchema.get_message_type("message_name") == "{};spec/schema/0.1/message_name".format(WriteSchema.MESSAGE_TYPE_DID)


def test_get_problem_report_message_type():
  writeSchema = WriteSchema(schema_name, schema_version, attrs[0], attrs[1], attrs[2])
  assert writeSchema.get_problem_report_message_type() == "{};spec/schema/0.1/problem-report".format(WriteSchema.MESSAGE_TYPE_DID)


def test_get_status_message_type():
  writeSchema = WriteSchema(schema_name, schema_version, attrs[0], attrs[1], attrs[2])
  assert writeSchema.get_status_message_type() == "{};spec/schema/0.1/status".format(WriteSchema.MESSAGE_TYPE_DID)


