# import json
#
# import pytest
#
# from test.test_utils import get_test_config, cleanup
# from verity_sdk.protocols.v1_0.IssueCredential import IssueCredential
# from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
# from verity_sdk.utils.Context import Context
#
# for_relationship = 'some_did'
# name = 'degree'
# cred_def_id = '12345'
# credential_values = {
#     'name': 'Joe',
#     'degree': 'Bachelors',
#     'gpa': '3.67'
# }
# price = '5'
#
#
# def test_init():
#     issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
#
#     assert issue_credential.for_relationship == for_relationship
#     assert issue_credential.name == name
#     assert issue_credential.cred_def_id == cred_def_id
#     assert json.dumps(issue_credential.credential_values) == json.dumps(credential_values)
#
#
# @pytest.mark.asyncio
# async def test_offer_credential():
#     context = await Context.create_with_config(await get_test_config())
#     issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
#     msg = issue_credential.offer_credential_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         IssueCredential.MSG_FAMILY,
#         IssueCredential.MSG_FAMILY_VERSION,
#         IssueCredential.OFFER_CREDENTIAL
#     )
#     assert msg['@id'] is not None
#     assert msg['~for_relationship'] == for_relationship
#     assert msg['~thread'] is not None
#     assert msg['~thread']['thid'] is not None
#     assert msg['name'] == name
#     assert msg['credDefId'] == cred_def_id
#     assert msg['credentialValues']['name'] == credential_values['name']
#     assert msg['credentialValues']['degree'] == credential_values['degree']
#     assert msg['credentialValues']['gpa'] == credential_values['gpa']
#     assert msg['price'] == price
#
#     await cleanup(context)
#
#
# @pytest.mark.asyncio
# async def test_issue_credential():
#     context = await Context.create_with_config(await get_test_config())
#     issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
#     msg = issue_credential.issue_credential_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         IssueCredential.MSG_FAMILY,
#         IssueCredential.MSG_FAMILY_VERSION,
#         IssueCredential.ISSUE_CREDENTIAL
#     )
#     assert msg['@id'] is not None
#     assert msg['~for_relationship'] == for_relationship
#     assert msg['~thread'] is not None
#     assert msg['~thread']['thid'] is not None
#
#     await cleanup(context)
#
#
# @pytest.mark.asyncio
# async def test_status():
#     context = await Context.create_with_config(await get_test_config())
#     issue_credential = IssueCredential(for_relationship, None, name, cred_def_id, credential_values, price)
#     msg = issue_credential.status_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         IssueCredential.MSG_FAMILY,
#         IssueCredential.MSG_FAMILY_VERSION,
#         IssueCredential.GET_STATUS
#     )
#     assert msg['@id'] is not None
#     assert msg['~for_relationship'] == for_relationship
#     assert msg['~thread'] is not None
#     assert msg['~thread']['thid'] is not None
#
#     await cleanup(context)
