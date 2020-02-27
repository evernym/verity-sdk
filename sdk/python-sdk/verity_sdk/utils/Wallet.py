import json
from indy.wallet import create_wallet
from indy.error import IndyError, ErrorCode


async def try_to_create_wallet(wallet_config, wallet_credentials):
    try:
        await create_wallet(wallet_config, wallet_credentials)
    except IndyError as e:
        if e.error_code != ErrorCode.WalletAlreadyExistsError:
            raise e


async def try_create_wallet(wallet_name, wallet_key, wallet_path=None):
    config_dict = { "id": wallet_name }
    if wallet_path:
        config_dict['path'] = wallet_path
    wallet_config = json.dumps(config_dict)
    wallet_credentials = json.dumps({"key": wallet_key})
    await try_to_create_wallet(wallet_config, wallet_credentials)
