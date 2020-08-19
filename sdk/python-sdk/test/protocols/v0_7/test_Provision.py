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

    test_token = """{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp":
    "2020-06-05T21:33:36.085Z", "sig":
    "ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey":
    "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"} """
    p = Provision(test_token)

    msg = p.provision_msg(context)

    assert msg['provisionToken'] is not None


@pytest.mark.asyncio
async def test_process_message():
    context = await Context.create_with_config(await get_test_config())
    p = Provision()

    async def mock_send(_, __):
        return {
            '@type': 'did:sov:123456789abcdefghi1234;spec/agent-provisioning/0.7/AGENT_CREATED',
            'selfDID': '4ut8tgCBdUMCYZrJh5JS4o',
            'agentVerKey': '38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE'
        }

    # pylint: disable=protected-access
    p._send_to_verity = mock_send

    context2 = await p.provision(context)
    assert context2.domain_did == '4ut8tgCBdUMCYZrJh5JS4o'
    assert context2.verity_agent_verkey == '38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE'


@pytest.mark.asyncio
async def test_validate_token():
    test_token = """{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp":
    "2020-06-05T21:33:36.085Z", "sig":
    "ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey":
    "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"} """
    await Provision.validate_token(test_token)

    test_token = """{"sponseeId": "myId", "sponsorId": "evernym-test-sponsorabc123", "nonce": "123", "timestamp":
    "2020-06-05T21:33:36.085Z", "sig":
    "AkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==", "sponsorVerKey":
    "GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL"} """
    with pytest.raises(Exception):
        await Provision.validate_token(test_token)
