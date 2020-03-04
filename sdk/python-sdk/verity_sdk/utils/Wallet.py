from indy.error import IndyError, ErrorCode
from indy.wallet import create_wallet, open_wallet


async def create_and_open_wallet(wallet_config, wallet_credentials):
    try:
        await create_wallet(wallet_config, wallet_credentials)
    except IndyError as e:
        if e.error_code != ErrorCode.WalletAlreadyExistsError:
            raise e

    return await open_wallet(wallet_config, wallet_credentials)


async def try_to_create_wallet(config, cred):
    try:
        await create_wallet(config, cred)
    except IndyError as e:
        if ErrorCode.WalletAlreadyExistsError == e.error_code:
            pass
        else:
            raise e
