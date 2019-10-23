import json
import pytest

from verity_sdk.protocols.IssueCredential import IssueCredential
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

for_relationship = 'some_did'
name = 'degree'
cred_def_id = '12345'
credential_values = {
  'name': 'Joe',
  'degree': 'Bachelors',
  'gpa': '3.67'
}
price = '5'


def test_init():
  issueCredential = IssueCredential(for_relationship, name, cred_def_id, credential_values, price)

  assert issueCredential.for_relationship == for_relationship
  assert issueCredential.name == name
  assert issueCredential.cred_def_id == cred_def_id
  assert json.dumps(issueCredential.credential_values) == json.dumps(credential_values)


@pytest.mark.asyncio
async def test_offer_credential():
  context = await Context.create(await get_test_config())
  issueCredential = IssueCredential(for_relationship, name, cred_def_id, credential_values, price)
  issueCredential.send = send_stub
  msg = await issueCredential.offer_credential(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    IssueCredential.MSG_FAMILY,
    IssueCredential.MSG_FAMILY_VERSION,
    IssueCredential.OFFER_CREDENTIAL
  )
  assert msg['@id'] is not None
  assert msg['~for_relationship'] == for_relationship
  assert msg['~thread'] is not None
  assert msg['~thread']['thid'] is not None
  assert msg['name'] == name
  assert msg['credDefId'] == cred_def_id
  assert msg['credentialValues']['name'] == credential_values['name']
  assert msg['credentialValues']['degree'] == credential_values['degree']
  assert msg['credentialValues']['gpa'] == credential_values['gpa']
  assert msg['price'] == price

  await cleanup(context)

@pytest.mark.asyncio
async def test_issue_credential():
  context = await Context.create(await get_test_config())
  issueCredential = IssueCredential(for_relationship, name, cred_def_id, credential_values, price)
  issueCredential.send = send_stub
  msg = await issueCredential.issue_credential(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    IssueCredential.MSG_FAMILY,
    IssueCredential.MSG_FAMILY_VERSION,
    IssueCredential.ISSUE_CREDENTIAL
  )
  assert msg['@id'] is not None
  assert msg['~for_relationship'] == for_relationship
  assert msg['~thread'] is not None
  assert msg['~thread']['thid'] is not None

  await cleanup(context)

@pytest.mark.asyncio
async def test_status():
  context = await Context.create(await get_test_config())
  issueCredential = IssueCredential(for_relationship, name, cred_def_id, credential_values, price)
  issueCredential.send = send_stub
  msg = await issueCredential.status(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    IssueCredential.MSG_FAMILY,
    IssueCredential.MSG_FAMILY_VERSION,
    IssueCredential.GET_STATUS
  )
  assert msg['@id'] is not None
  assert msg['~for_relationship'] == for_relationship
  assert msg['~thread'] is not None
  assert msg['~thread']['thid'] is not None

  await cleanup(context)
