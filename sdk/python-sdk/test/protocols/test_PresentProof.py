import pytest

from verity_sdk.protocols.v1_0.PresentProof import PresentProof
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER

for_relationship = 'some_did'
thread_id = 'some thread id'
name = 'Degree Verification'
proof_attrs = [
    {'name': 'name', 'restrictions': [{'issuer_did': 'did:sov:12345'}]},
    {'name': 'degree', 'restrictions': [{'issuer_did': 'did:sov:12345'}]},
    {'names': ['first_name', 'last_name'], 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
]
proof_predicates = [
    {'name': 'age', 'p_type': 'GT', 'p_value': 18, 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
]
revocation_interval = {'support_revocation': False}
reject_reason = 'because'


def test_init():
    present_proof = PresentProof(for_relationship, thread_id, name, proof_attrs, proof_predicates)

    assert present_proof.for_relationship == for_relationship
    assert present_proof.thread_id == thread_id
    assert present_proof.name == name
    assert present_proof.proof_attrs == proof_attrs
    assert present_proof.proof_predicates == proof_predicates


@pytest.mark.asyncio
async def test_request():
    present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates)
    msg = present_proof.request_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.PROOF_REQUEST
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['name'] == name
    assert msg['proof_attrs'] == proof_attrs
    assert msg['proof_predicates'] == proof_predicates


@pytest.mark.asyncio
async def test_status():
    present_proof = PresentProof(for_relationship, thread_id)
    msg = present_proof.status_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.STATUS
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id


@pytest.mark.asyncio
async def test_accept_proposal():
    present_proof = PresentProof(for_relationship, thread_id)
    msg = present_proof.accept_proposal_msg()

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.ACCEPT_PROPOSAL
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id


@pytest.mark.asyncio
async def test_reject():
    present_proof = PresentProof(for_relationship, thread_id)
    msg = present_proof.reject_msg(reject_reason)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        COMMUNITY_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.REJECT
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] == thread_id
    assert msg['reason'] == reject_reason
