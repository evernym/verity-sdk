from indy.did import create_and_store_my_did
from indy.error import IndyError


async def create_new_did(wallet_handle: int):
    try:
        (did, verkey) = await create_and_store_my_did(wallet_handle, '{}')
        return Did(did, verkey)
    except IndyError as e:
        raise Exception('Unable to create DID with wallet', e)


class Did:
    did: str
    verkey: str

    def __init__(self, did, verkey):
        self.did = did
        self.verkey = verkey
