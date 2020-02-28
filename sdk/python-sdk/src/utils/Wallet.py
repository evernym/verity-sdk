import json
from indy.wallet import create_wallet, open_wallet
from indy.error import IndyError, ErrorCode


async def create_and_open_wallet(wallet_config, wallet_credentials):
    try:
        await create_wallet(wallet_config, wallet_credentials)
    except IndyError as e:
        if e.error_code != ErrorCode.WalletAlreadyExistsError:
            raise e

    return await open_wallet(wallet_config, wallet_credentials)