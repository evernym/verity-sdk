import pytest

from verity_sdk.protocols.PresentProof import PresentProof
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

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
  presentProof = PresentProof(for_relationship, name, proof_attrs, proof_predicates, revocation_interval)

  assert presentProof.for_relationship == for_relationship
  assert presentProof.name == name
  assert presentProof.proof_attrs == proof_attrs
  assert presentProof.proof_predicates == proof_predicates
  assert presentProof.revocation_interval == revocation_interval


@pytest.mark.asyncio
async def test_request():
  context = await Context.create(await get_test_config())
  presentProof = PresentProof(for_relationship, name, proof_attrs, proof_predicates, revocation_interval)
  presentProof.send = send_stub
  msg = await presentProof.request(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
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
  context = await Context.create(await get_test_config())
  presentProof = PresentProof(for_relationship, name, proof_attrs, proof_predicates, revocation_interval)
  presentProof.send = send_stub
  msg = await presentProof.status(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    PresentProof.MSG_FAMILY,
    PresentProof.MSG_FAMILY_VERSION,
    PresentProof.GET_STATUS
  )
  assert msg['@id'] is not None
  assert msg['~for_relationship'] == for_relationship
  assert msg['~thread'] is not None
  assert msg['~thread']['thid'] is not None

  await cleanup(context)
