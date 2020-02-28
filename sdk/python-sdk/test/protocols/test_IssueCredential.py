import json

import pytest

from test.test_utils import get_test_config, send_stub, cleanup
from src.protocols.IssueCredential import IssueCredential
from src.utils import unpack_forward_message, EVERNYM_MSG_QUALIFIER
from src.utils.Context import Context

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
    issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)

    assert issue_credential.for_relationship == for_relationship
    assert issue_credential.name == name
    assert issue_credential.cred_def_id == cred_def_id
    assert json.dumps(issue_credential.credential_values) == json.dumps(credential_values)


@pytest.mark.asyncio
async def test_offer_credential():
    context = await Context.create(await get_test_config())
    issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
    issue_credential.send = send_stub
    msg = await issue_credential.offer_credential(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
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
    issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
    issue_credential.send = send_stub
    msg = await issue_credential.issue_credential(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
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
    issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
    issue_credential.send = send_stub
    msg = await issue_credential.status(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.GET_STATUS
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None

    await cleanup(context)
