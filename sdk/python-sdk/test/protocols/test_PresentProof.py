import pytest

from test.test_utils import get_test_config, send_stub, cleanup
from src.protocols.PresentProof import PresentProof
from src.utils import unpack_forward_message, EVERNYM_MSG_QUALIFIER
from src.utils.Context import Context

for_relationship = 'some_did'
name = 'Degree Verification'
proof_attrs = [
    {'name': 'name', 'restrictions': [{'issuer_did': 'did:sov:12345'}]},
    {'name': 'degree', 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
]
proof_predicates = [
    {'name': 'age', 'p_type': 'GT', 'p_value': 18, 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
]
revocation_interval = {'support_revocation': False}


def test_init():
    present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates, revocation_interval)

    assert present_proof.for_relationship == for_relationship
    assert present_proof.name == name
    assert present_proof.proof_attrs == proof_attrs
    assert present_proof.proof_predicates == proof_predicates
    assert present_proof.revocation_interval == revocation_interval


@pytest.mark.asyncio
async def test_request():
    context = await Context.create_with_config(await get_test_config())
    present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates, revocation_interval)
    present_proof.send = send_stub
    msg = await present_proof.request(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.PROOF_REQUEST
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['name'] == name
    assert msg['proofAttrs'] == proof_attrs
    assert msg['proofPredicates'] == proof_predicates
    assert msg['revocationInterval'] == revocation_interval

    await cleanup(context)


@pytest.mark.asyncio
async def test_status():
    context = await Context.create_with_config(await get_test_config())
    present_proof = PresentProof(for_relationship, None, name, proof_attrs, proof_predicates, revocation_interval)
    present_proof.send = send_stub
    msg = await present_proof.status(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        PresentProof.MSG_FAMILY,
        PresentProof.MSG_FAMILY_VERSION,
        PresentProof.GET_STATUS
    )
    assert msg['@id'] is not None
    assert msg['~for_relationship'] == for_relationship
    assert msg['~thread'] is not None
    assert msg['~thread']['thid'] is not None

    await cleanup(context)
