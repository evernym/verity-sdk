import pytest

from verity_sdk.protocols.PresentProof import PresentProof
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

connection_id = '12345'
name = 'Degree Verification'
proof_attrs = [
  {'name': 'name', 'restrictions': [{'issuer_did': 'did:sov:12345'}]},
  {'name': 'degree', 'restrictions': [{'issuer_did': 'did:sov:12345'}]}
]
revocation_interval = {'support_revocation': False}


def test_init():
  presentProof = PresentProof(connection_id, name, proof_attrs, revocation_interval)

  assert presentProof.connection_id == connection_id
  assert presentProof.name == name
  assert presentProof.proof_attrs == proof_attrs
  assert presentProof.revocation_interval == revocation_interval


@pytest.mark.asyncio
async def test_request():
  context = await Context.create(await get_test_config())
  presentProof = PresentProof(connection_id, name, proof_attrs, revocation_interval)
  presentProof.send = send_stub
  msg = await presentProof.request(context)
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/present-proof/0.1/request'.format(MESSAGE_TYPE_DID)
  assert msg['@id'] is not None
  assert msg['connectionId'] == connection_id
  assert msg['proofRequest']['name'] == name
  assert msg['proofRequest']['proofAttrs'] == proof_attrs
  assert msg['proofRequest']['revocationInterval'] == revocation_interval

  await cleanup(context)
