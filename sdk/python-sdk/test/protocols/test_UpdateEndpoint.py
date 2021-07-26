import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v0_6.UpdateEndpoint import UpdateEndpoint
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


@pytest.mark.asyncio
async def test_update_endpoint():
    context = await Context.create_with_config(await get_test_config())
    update_endpoint = UpdateEndpoint()
    msg = update_endpoint.update_endpoint_msg(context)

    assert msg['@type'] == '{}/configs/0.6/UPDATE_COM_METHOD'.format(EVERNYM_MSG_QUALIFIER)
    assert msg['@id'] is not None
    assert msg['comMethod']['id'] == 'webhook'
    assert msg['comMethod']['type'] == 2
    assert msg['comMethod']['value'] == context.endpoint_url
    assert msg['comMethod']['packaging']['pkgType'] == '1.0'
    assert msg['comMethod']['packaging']['recipientKeys'] == [context.sdk_verkey]

    await cleanup(context)


@pytest.mark.asyncio
async def test_update_endpoint_with_oauth2_v1_authentication():
    context = await Context.create_with_config(await get_test_config())
    update_endpoint = UpdateEndpoint(
        {
            'type': 'OAuth2',
            'version': 'v1',
            'data': {
                'url': 'https://auth.url/token',
                'grant_type': 'client_credentials',
                'client_id': 'ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA',
                'client_secret': 'aaGxxcGi6kb6AxIe'
            }
        }
    )
    msg = update_endpoint.update_endpoint_msg(context)

    assert msg['@type'] == '{}/configs/0.6/UPDATE_COM_METHOD'.format(EVERNYM_MSG_QUALIFIER)
    assert msg['@id'] is not None
    assert msg['comMethod']['id'] == 'webhook'
    assert msg['comMethod']['type'] == 2
    assert msg['comMethod']['value'] == context.endpoint_url
    assert msg['comMethod']['packaging']['pkgType'] == '1.0'
    assert msg['comMethod']['packaging']['recipientKeys'] == [context.sdk_verkey]

    # authentication
    assert msg['comMethod']['authentication']['type'] == 'OAuth2'
    assert msg['comMethod']['authentication']['version'] == 'v1'
    assert msg['comMethod']['authentication']['data']['url'] == 'https://auth.url/token'
    assert msg['comMethod']['authentication']['data']['grant_type'] == 'client_credentials'
    assert msg['comMethod']['authentication']['data']['client_id'] == 'ajeyKDizsDkwYeEmRmHU78gf7W3VIEoA'
    assert msg['comMethod']['authentication']['data']['client_secret'] == 'aaGxxcGi6kb6AxIe'

    await cleanup(context)
