import pytest

from test.test_utils import get_test_config
from verity_sdk.protocols.v0_7.Provision import Provision
from verity_sdk.utils.Context import Context


@pytest.mark.asyncio
async def test_message():
    context = await Context.create_with_config(await get_test_config())
    p = Provision()

    msg = p.provision_msg(context)

    assert msg['requesterVk'] is not None
    assert msg['requesterVk'] == context.sdk_verkey
    assert msg['@type'] == 'did:sov:123456789abcdefghi1234;spec/agent-provisioning/0.7/create-edge-agent'


@pytest.mark.asyncio
async def test_process_message():
    context = await Context.create_with_config(await get_test_config())
    p = Provision()

    async def mock_send(_, __):
        return {
            'selfDID': '4ut8tgCBdUMCYZrJh5JS4o',
            'agentVerKey': '38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE'
        }

    p.send_to_verity = mock_send

    context2 = await p.provision(context)
    assert context2.domain_did == '4ut8tgCBdUMCYZrJh5JS4o'
    assert context2.verity_agent_verkey == '38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE'
