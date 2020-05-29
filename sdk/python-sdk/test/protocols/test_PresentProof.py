# import pytest
#
# from test.test_utils import get_test_config, cleanup
# from verity_sdk.protocols.v1_0.PresentProof import PresentProof
# from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
# from verity_sdk.utils.Context import Context
#
# for_relationship = 'some_did'
# name = 'Degree Verification'
# proof_attrs = [
#     {'name': 'name', 'restrictions': [{'issuer_did': 'did:sov:12345'}]},
#     {'name': 'degree', 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
# ]
# proof_predicates = [
#     {'name': 'age', 'p_type': 'GT', 'p_value': 18, 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
# ]
# revocation_interval = {'support_revocation': False}
#
#
# def test_init():
#     present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates)
#
#     assert present_proof.for_relationship == for_relationship
#     assert present_proof.name == name
#     assert present_proof.proof_attrs == proof_attrs
#     assert present_proof.proof_predicates == proof_predicates
#
#
# @pytest.mark.asyncio
# async def test_request():
#     context = await Context.create_with_config(await get_test_config())
#     present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates)
#     msg = present_proof.request_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         PresentProof.MSG_FAMILY,
#         PresentProof.MSG_FAMILY_VERSION,
#         PresentProof.PROOF_REQUEST
#     )
#     assert msg['@id'] is not None
#     assert msg['~for_relationship'] == for_relationship
#     assert msg['~thread'] is not None
#     assert msg['~thread']['thid'] is not None
#     assert msg['name'] == name
#     assert msg['proofAttrs'] == proof_attrs
#     assert msg['proofPredicates'] == proof_predicates
#
#     await cleanup(context)
#
#
# @pytest.mark.asyncio
# async def test_status():
#     context = await Context.create_with_config(await get_test_config())
#     present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates)
#     msg = present_proof.status_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         PresentProof.MSG_FAMILY,
#         PresentProof.MSG_FAMILY_VERSION,
#         PresentProof.GET_STATUS
#     )
#     assert msg['@id'] is not None
#     assert msg['~for_relationship'] == for_relationship
#     assert msg['~thread'] is not None
#     assert msg['~thread']['thid'] is not None
#
#     await cleanup(context)
