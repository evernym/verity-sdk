import json
import pytest

from verity_sdk.protocols.IssueCredential import IssueCredential
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

connection_id = 'connection id'
name = 'degree'
cred_def_id = '12345'
credential_values = {
  'name': 'Joe',
  'degree': 'Bachelors',
  'gpa': '3.67'
}
price = 5


def test_init():
  issueCredential = IssueCredential(connection_id, name, cred_def_id, credential_values, price)

  assert issueCredential.connection_id == connection_id
  assert issueCredential.name == name
  assert issueCredential.cred_def_id == cred_def_id
  assert json.dumps(issueCredential.credential_values) == json.dumps(credential_values)


@pytest.mark.asyncio
async def test_issue():
  context = await Context.create(await get_test_config())
  issueCredential = IssueCredential(connection_id, name, cred_def_id, credential_values, price)
  issueCredential.send = send_stub
  msg = await issueCredential.issue(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/issue-credential/0.1/issue-credential'.format(MESSAGE_TYPE_DID)
  assert msg['@id'] is not None
  assert msg['connectionId'] == connection_id
  assert msg['credentialData']['id']
  assert msg['credentialData']['name'] == name
  assert msg['credentialData']['credDefId'] == cred_def_id
  assert msg['credentialData']['credentialValues']['name'] == credential_values['name']
  assert msg['credentialData']['credentialValues']['degree'] == credential_values['degree']
  assert msg['credentialData']['credentialValues']['gpa'] == credential_values['gpa']
  assert msg['credentialData']['price'] == price

  await cleanup(context)
