import json
import pytest

from indy import crypto
from verity_sdk.protocols.Provision import Provision
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context
from test.test_utils import get_test_config, cleanup

pw_did = 'pwDID'
pw_verkey = 'pwVerKey'


def test_init():
  Provision()


@pytest.mark.asyncio
async def test_provision_sdk(mocker):
  context = await Context.create(await get_test_config())

  response = {
    'withPairwiseDID': pw_did,
    'withPairwiseDIDVerKey': pw_verkey
  }
  response_msg = await crypto.pack_message(
    context.wallet_handle,
    json.dumps(response),
    [context.sdk_pairwise_verkey],
    context.verity_public_verkey,
  )
  mock = mocker.patch('verity_sdk.protocols.Provision.send_message')
  mock.return_value = response_msg

  provision = Provision()
  context = await provision.provision_sdk(context)

  assert context.verity_pairwise_did == pw_did
  assert context.verity_pairwise_verkey == pw_verkey

  mock.assert_called_once()
  call_args = mock.call_args
  url = call_args[0][0]
  sent_msg = call_args[0][1]

  assert url == context.verity_url

  sent_msg = await unpack_forward_message(context, sent_msg)
  assert sent_msg['@type'] == '{};spec/{}/{}/{}'.format(
    MESSAGE_TYPE_DID,
    Provision.MSG_FAMILY,
    Provision.MSG_FAMILY_VERSION,
    Provision.CREATE_AGENT
  )
  assert sent_msg['@id'] is not None
  assert sent_msg['fromDID'] == context.sdk_pairwise_did
  assert sent_msg['fromDIDVerKey'] == context.sdk_pairwise_verkey

  await cleanup(context)
