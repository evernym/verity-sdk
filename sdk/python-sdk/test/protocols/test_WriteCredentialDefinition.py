import pytest

from verity_sdk.protocols.WriteCredentialDefinition import WriteCredentialDefinition
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

name = 'name'
schema_id = 'schema id'
tag = 'tag'
revocation_details = {'support_revocation': False}


def test_init():
  writeCredDef = WriteCredentialDefinition(name, schema_id, tag, revocation_details)

  assert writeCredDef.name == name
  assert writeCredDef.schema_id == schema_id
  assert writeCredDef.tag == tag
  assert writeCredDef.revocation_details == revocation_details
  assert len(writeCredDef.messages) == 1


@pytest.mark.asyncio
async def test_write():
  context = await Context.create(await get_test_config())
  writeCredDef = WriteCredentialDefinition(name, schema_id, tag, revocation_details)
  writeCredDef.send = send_stub
  msg = await writeCredDef.write(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/write-cred-def/0.1/write'.format(MESSAGE_TYPE_DID)
  assert msg['@id'] is not None
  assert msg['name'] == name
  assert msg['schemaId'] == schema_id
  assert msg['tag'] == tag
  assert msg['revocationDetails'] == revocation_details

  await cleanup(context)
