import json
from uuid import uuid4 as uuid
import pytest
from indy import wallet
from verity_sdk.utils import _prepare_forward_message, pack_message_for_verity, \
    unpack_forward_message, get_message_type, get_problem_report_message_type, \
    EVERNYM_MSG_QUALIFIER, get_status_message_type
from verity_sdk.utils.Context import Context
from verity_sdk.utils.Did import create_new_did


async def get_test_config(seed=None):
    test_config = {
        'walletName': str(uuid()),
        'walletKey': str(uuid()),
        'verityUrl': 'http://localhost:8080/agency/msg',
        'verityPublicVerkey': None,
        'verityPairwiseDID': None,
        'verityPairwiseVerkey': None,
        'sdkPairwiseVerkey': None,
        'endpointUrl': 'http://localhost:4000'
    }

    wallet_config = json.dumps({'id': test_config['walletName']})
    wallet_credentials = json.dumps({'key': test_config['walletKey']})
    await wallet.create_wallet(wallet_config, wallet_credentials)
    wallet_handle = await wallet.open_wallet(wallet_config, wallet_credentials)
    their_public = await create_new_did(wallet_handle)
    their = await create_new_did(wallet_handle)
    my = await create_new_did(wallet_handle, seed)
    await wallet.close_wallet(wallet_handle)

    test_config['verityPublicDID'] = their_public.did
    test_config['verityPublicVerkey'] = their_public.verkey
    test_config['verityPairwiseDID'] = their.did
    test_config['verityPairwiseVerkey'] = their.verkey
    test_config['sdkPairwiseDID'] = my.did
    test_config['sdkPairwiseVerkey'] = my.verkey

    return json.dumps(test_config)


async def cleanup(context: Context):
    if not context.wallet_closed:
        await context.close_wallet()
    await wallet.delete_wallet(context.wallet_config, context.wallet_credentials)


@pytest.mark.asyncio
async def test_context():
    test_config = await get_test_config()

    context = await Context.create_with_config(test_config)
    test_config = json.loads(test_config)
    assert context.wallet_name == test_config['walletName']
    assert context.wallet_key == test_config['walletKey']
    assert context.verity_url == test_config['verityUrl']
    assert context.verity_public_verkey == test_config['verityPublicVerkey']
    assert context.domain_did == test_config['verityPairwiseDID']
    assert context.verity_agent_verkey == test_config['verityPairwiseVerkey']
    assert context.sdk_verkey == test_config['sdkPairwiseVerkey']
    assert context.endpoint_url == test_config['endpointUrl']

    await cleanup(context)


@pytest.mark.asyncio
async def test_v01_to_v02():
    wallet_name = uuid()
    wallet_key = uuid()
    v01Str = f"""{{
      "verityPublicVerkey": "ETLgZKeQEKxBW7gXA6FBn7nBwYhXFoogZLCCn5EeRSQV",
      "verityPairwiseDID": "NTvSuSXzygyxWrF3scrhdc",
      "verityUrl": "https://vas-team1.pdev.evernym.com",
      "verityPairwiseVerkey": "ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE",
      "walletName": "{wallet_name}",
      "walletKey": "{wallet_key}",
      "sdkPairwiseVerkey": "HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh",
      "verityPublicDID": "Rgj7LVEonrMzcRC1rhkx76",
      "sdkPairwiseDID": "XNRkA8tboikwHD3x1Yh7Uz"
    }}"""

    wallet_config = json.dumps({'id': str(wallet_name)})
    wallet_credentials = json.dumps({'key': str(wallet_key)})
    await wallet.create_wallet(wallet_config, wallet_credentials)

    ctx = await Context.create_with_config(v01Str)
    await ctx.close_wallet()

    assert ctx.domain_did == 'NTvSuSXzygyxWrF3scrhdc'
    assert ctx.verity_agent_verkey == 'ChXRWjQdrrLyksbPQZfaS3JekA4xLgD5Jg7GzXhc9zqE'
    assert ctx.sdk_verkey == 'HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh'
    assert ctx.sdk_verkey_id == 'XNRkA8tboikwHD3x1Yh7Uz'
    assert ctx.version == '0.2'


def test_prepare_forward_message():
    test_did = '123'
    message = b'{"hello": "world"}'
    msg = _prepare_forward_message(test_did, message)
    msg = json.loads(msg)

    assert msg['@type'] == 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD'
    assert msg['@fwd'] == test_did
    assert msg['@msg'] == json.loads(message.decode('utf-8'))


@pytest.mark.asyncio
async def test_pack_message_for_verity_and_unpack_forward_message():
    message = {'hello': 'world'}
    context = await Context.create_with_config(await get_test_config())
    packed_message = await pack_message_for_verity(context, message)
    unpacked_message = await unpack_forward_message(context, packed_message)
    assert unpacked_message['hello'] == 'world'

    await cleanup(context)


def test_get_message_type():
    msg_type: str = 'did:sov:123456789abcdefghi1234;spec/credential/0.1/status'
    assert get_message_type('credential', '0.1', 'status') == msg_type


def test_get_problem_report_message_type():
    assert get_problem_report_message_type('credential', '0.1') == \
           '{};spec/credential/0.1/problem-report'.format(EVERNYM_MSG_QUALIFIER)


def test_get_status_message_type():
    assert get_status_message_type('credential', '0.1') == \
           '{};spec/credential/0.1/status'.format(EVERNYM_MSG_QUALIFIER)
