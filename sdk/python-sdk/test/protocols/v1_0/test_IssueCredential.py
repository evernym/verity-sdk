import json

import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v1_0.IssueCredential import IssueCredential
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Context import Context

for_relationship = 'some_did'
comment = 'degree'
cred_def_id = '12345'
credential_values = {
    'name': 'Joe',
    'degree': 'Bachelors',
    'gpa': '3.67'
}
price = '0'
auto_issue = True
by_invitation = True
thread_id = 'some thread id'


def test_init():
    issue_credential = IssueCredential(
        for_relationship, thread_id, cred_def_id, credential_values, comment, price, auto_issue, by_invitation
    )

    assert issue_credential.for_relationship == for_relationship
    assert issue_credential.thread_id == thread_id
    assert issue_credential.cred_def_id == cred_def_id
    assert json.dumps(issue_credential.values) == json.dumps(credential_values)
    assert issue_credential.comment == comment
    assert issue_credential.price == price
    assert issue_credential.auto_issue == auto_issue
    assert issue_credential.by_invitation == by_invitation


@pytest.mark.asyncio
async def test_propose_credential():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(
        for_relationship, None, cred_def_id, credential_values, comment
    )
    msg = issue_credential.propose_credential_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.PROPOSE
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['cred_def_id'] == cred_def_id
    assert msg['credential_values']['name'] == credential_values['name']
    assert msg['credential_values']['degree'] == credential_values['degree']
    assert msg['credential_values']['gpa'] == credential_values['gpa']
    assert msg['comment'] == comment

    await cleanup(context)


@pytest.mark.asyncio
async def test_offer_credential():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(
        for_relationship, None, cred_def_id, credential_values, comment, price, auto_issue, by_invitation
    )
    msg = issue_credential.offer_credential_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.OFFER
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['cred_def_id'] == cred_def_id
    assert msg['credential_values']['name'] == credential_values['name']
    assert msg['credential_values']['degree'] == credential_values['degree']
    assert msg['credential_values']['gpa'] == credential_values['gpa']
    assert msg['comment'] == comment
    assert msg['price'] == price
    assert msg['auto_issue'] == auto_issue
    assert msg['by_invitation'] == by_invitation

    await cleanup(context)


@pytest.mark.asyncio
async def test_request_credential():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(for_relationship, thread_id, cred_def_id, None, comment)
    msg = issue_credential.request_credential_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.REQUEST
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id
    assert msg['cred_def_id'] == cred_def_id
    assert msg['comment'] == comment

    await cleanup(context)


@pytest.mark.asyncio
async def test_reject_credential():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(for_relationship, thread_id, None, None, comment)
    msg = issue_credential.reject_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.REJECT
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id
    assert msg['comment'] == comment

    await cleanup(context)

@pytest.mark.asyncio
async def test_issue_credential():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(for_relationship, thread_id)
    msg = issue_credential.issue_credential_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.ISSUE
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id

    await cleanup(context)


@pytest.mark.asyncio
async def test_status():
    context = await Context.create_with_config(await get_test_config())
    issue_credential = IssueCredential(for_relationship, thread_id)
    msg = issue_credential.status_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        IssueCredential.MSG_FAMILY,
        IssueCredential.MSG_FAMILY_VERSION,
        IssueCredential.STATUS
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id

    await cleanup(context)
