import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v0_6.UpdateConfigs import UpdateConfigs
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context

name = 'Name1'
logo_url = 'http://logo.url'


def test_init():
    update_configs = UpdateConfigs(name, logo_url)

    assert update_configs.name == name
    assert update_configs.logo_url == logo_url


@pytest.mark.asyncio
async def test_update():
    context = await Context.create_with_config(await get_test_config())
    update_configs = UpdateConfigs(name, logo_url)
    msg = update_configs.update_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        UpdateConfigs.MSG_FAMILY,
        UpdateConfigs.MSG_FAMILY_VERSION,
        UpdateConfigs.UPDATE_CONFIGS
    )
    assert msg['@id'] is not None
    assert msg['configs'] == [
        {'name': 'name', 'value': name},
        {'name': 'logoUrl', 'value': logo_url}
    ]

    await cleanup(context)


@pytest.mark.asyncio
async def test_status():
    context = await Context.create_with_config(await get_test_config())
    update_configs = UpdateConfigs()
    msg = update_configs.status_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        UpdateConfigs.MSG_FAMILY,
        UpdateConfigs.MSG_FAMILY_VERSION,
        UpdateConfigs.GET_STATUS
    )
    assert msg['@id'] is not None

    await cleanup(context)
