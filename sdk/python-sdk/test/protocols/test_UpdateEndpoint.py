import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v0_6.UpdateEndpoint import UpdateEndpoint
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


@pytest.mark.asyncio
async def test_write():
    context = await Context.create_with_config(await get_test_config())
    update_endpoint = UpdateEndpoint()
    msg = update_endpoint.update_endpoint_msg(context)

    assert msg['@type'] == '{};spec/configs/0.6/UPDATE_COM_METHOD'.format(EVERNYM_MSG_QUALIFIER)
    assert msg['@id'] is not None
    assert msg['comMethod']['id'] == 'webhook'
    assert msg['comMethod']['type'] == 2
    assert msg['comMethod']['value'] == context.endpoint_url
    assert msg['comMethod']['packaging']['pkgType'] == '1.0'
    assert msg['comMethod']['packaging']['recipientKeys'] == [context.sdk_verkey]

    await cleanup(context)
