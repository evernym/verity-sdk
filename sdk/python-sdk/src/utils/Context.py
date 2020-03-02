import json
import requests

from indy import wallet

from src.utils.Wallet import create_and_open_wallet, try_to_create_wallet


class Context:
    verity_url: str
    verity_public_did: str
    verity_public_verkey: str
    verity_pairwise_did: str
    verity_pairwise_verkey: str
    sdk_pairwise_did: str
    sdk_pairwise_verkey: str
    endpoint_url: str
    wallet_name: str
    wallet_path: str
    wallet_key: str
    wallet_config: str
    wallet_credentials: str
    wallet_handle: int
    wallet_closed: bool

    @classmethod
    async def create(cls, wallet_name: str, wallet_key: str, verity_url: str, endpoint_url: str, wallet_path: str = None):
        context = cls()
        context.set_wallet_config(wallet_name, wallet_path)
        context.set_wallet_credentials(wallet_key)
        context.wallet_name = wallet_name
        context.wallet_key = wallet_key
        context.verity_url = verity_url
        context.endpoint_url = endpoint_url
        await context.update_verity_info()
        await context.open_wallet()
        return context

    def set_wallet_config(self, wallet_name, wallet_path = None):
        wallet_config = {'id': wallet_name}

        if wallet_path:
            wallet_config['storage_config'] = {'path': wallet_path}

        self.wallet_config = json.dumps(wallet_config)

    def set_wallet_credentials(self, wallet_key):
        self.wallet_credentials = json.dumps({'key': wallet_key})

    async def update_verity_info(self):
        full_url = f"{self.verity_url}/agency"
        result = requests.get(full_url)
        status_code = result.status_code
        if status_code > 399:
            raise IOError(f"Request failed! - {status_code} - {result.text}")
        else:
            try:
                msg = result.json()
                self.verity_public_did = msg.get("DID")
                self.verity_public_verkey = msg.get("verKey")
            except ValueError as e:
                raise IOError(f"Invalid and unexpected data from Verity -- response -- {e}")

    async def open_wallet(self):
        self.wallet_handle = await create_and_open_wallet(self.wallet_config, self.wallet_credentials)

    @classmethod
    async def create_with_config(cls, config):
        context = cls()
        config = json.loads(config)

        context.wallet_name = config['walletName']
        context.wallet_key = config['walletKey']
        context.wallet_path = config.get('walletPath')
        context.verity_url = config['verityUrl']
        context.verity_public_did = config.get('verityPublicDID')
        context.verity_public_verkey = config.get('verityPublicVerkey')
        context.verity_pairwise_did = config.get('verityPairwiseDID')
        context.verity_pairwise_verkey = config.get('verityPairwiseVerkey')
        context.sdk_pairwise_did = config.get('sdkPairwiseDID')
        context.sdk_pairwise_verkey = config.get('sdkPairwiseVerkey')
        context.endpoint_url = config.get('endpointUrl')

        context.set_wallet_config(context.wallet_name, context.wallet_path)
        context.set_wallet_credentials(context.wallet_key)

        # Ensure the wallet exists
        await try_to_create_wallet(context.wallet_config, context.wallet_credentials)

        context.wallet_handle = await wallet.open_wallet(
            context.wallet_config,
            context.wallet_credentials
        )
        context.wallet_closed = False

        return context

    async def close_wallet(self):
        await wallet.close_wallet(self.wallet_handle)
        self.wallet_closed = True

    def to_json(self) -> str:
        return json.dumps(
            {
                'walletName': self.wallet_name,
                'walletKey': self.wallet_key,
                'walletPath': self.wallet_path if hasattr(self, 'wallet_path') else None,
                'verityUrl': self.verity_url,
                'verityPublicDID': self.verity_public_did,
                'verityPublicVerkey': self.verity_public_verkey,
                'verityPairwiseDID': self.verity_pairwise_did,
                'verityPairwiseVerkey': self.verity_pairwise_verkey,
                'sdkPairwiseDID': self.sdk_pairwise_did,
                'sdkPairwiseVerkey': self.sdk_pairwise_verkey,
                'endpointUrl': self.endpoint_url,
            }
        )