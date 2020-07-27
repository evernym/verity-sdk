import json

from indy.did import create_and_store_my_did
from indy.error import IndyError


async def create_new_did(wallet_handle: int, seed: str = None):
    """
    Creates a new generic DID in the given wallet based on a given seed
    Args:
        wallet_handle (int):  handle to an created and opened indy wallet
        seed (str):  a 32 character seed string used to deterministically create a key for the DID
    Returns:
         a DID object based on the DID created in the wallet
    """
    try:
        params = {}
        if seed:
            params['seed'] = seed

        (did, verkey) = await create_and_store_my_did(wallet_handle, json.dumps(params))
        return Did(did, verkey)
    except IndyError as e:
        raise Exception('Unable to create DID with wallet', e)


class Did:
    """
    Simple holder object for a DID
    """
    did: str
    verkey: str

    def __init__(self, did, verkey):
        self.did = did
        self.verkey = verkey
