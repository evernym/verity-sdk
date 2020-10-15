from indy.error import IndyError, ErrorCode
from indy.wallet import create_wallet, open_wallet


async def create_and_open_wallet(wallet_config, wallet_credentials):
    """
    Tries to create a wallet with the given parameters. If creation fails because the wallet already exists, that
    error is trapped and this function will complete but other errors will be raised. The created or the already
    existing wallet is then opened and the handle is returned.

    Args:
        wallet_config: indy wallet config
        wallet_credentials: indy wallet credential
    Returns:
        int: indy wallet handle
    """
    try:
        await create_wallet(wallet_config, wallet_credentials)
    except IndyError as e:
        if e.error_code != ErrorCode.WalletAlreadyExistsError:
            raise e

    return await open_wallet(wallet_config, wallet_credentials)
