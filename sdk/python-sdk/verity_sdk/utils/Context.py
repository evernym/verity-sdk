import json
import requests

from indy import wallet

from verity_sdk.utils import Did
from verity_sdk.utils.Wallet import create_and_open_wallet, try_to_create_wallet

V_0_1 = "0.1"
V_0_2 = "0.2"


class Context:
    verity_url: str
    verity_public_did: str
    verity_public_verkey: str
    domain_did: str
    verity_agent_verkey: str
    sdk_verkey_id: str
    sdk_verkey: str
    endpoint_url: str
    wallet_name: str
    wallet_path: str
    wallet_key: str
    wallet_config: str
    wallet_credentials: str
    wallet_handle: int
    wallet_closed: bool
    version: str = V_0_2

    @classmethod
    async def create(cls, wallet_name: str,
                     wallet_key: str,
                     verity_url: str,
                     domain_did: str = None,
                     verity_agent_verkey: str = None,
                     endpoint_url: str = None,
                     seed: str = None,
                     wallet_path: str = None):
        context = cls()
        context.set_wallet_config(wallet_name, wallet_path)
        context.set_wallet_credentials(wallet_key)
        context.wallet_name = wallet_name
        context.wallet_key = wallet_key
        context.verity_url = verity_url

        await context.update_verity_info()
        await context.open_wallet()

        context.endpoint_url = endpoint_url

        context.domain_did = domain_did

        context.verity_agent_verkey = verity_agent_verkey

        sdk_key = await Did.create_new_did(context.wallet_handle, seed=seed)

        context.sdk_verkey_id = sdk_key.did
        context.sdk_verkey = sdk_key.verkey

        return context

    def set_wallet_config(self, wallet_name, wallet_path=None):
        wallet_config = {'id': wallet_name}

        if wallet_path:
            wallet_config['storage_config'] = {'path': wallet_path}

        self.wallet_config = json.dumps(wallet_config)

    def set_wallet_credentials(self, wallet_key):
        self.wallet_credentials = json.dumps({'key': wallet_key})

    async def update_verity_info(self):
        full_url = f'{self.verity_url}/agency'
        result = requests.get(full_url)
        status_code = result.status_code
        if status_code > 399:
            raise IOError(f'Request failed! - {status_code} - {result.text}')

        try:
            msg = result.json()
            self.verity_public_did = msg.get('DID')
            self.verity_public_verkey = msg.get('verKey')
        except ValueError as e:
            raise IOError(f'Invalid and unexpected data from Verity -- response -- {e}')

    async def open_wallet(self):
        self.wallet_handle = await create_and_open_wallet(self.wallet_config, self.wallet_credentials)

    @classmethod
    async def create_with_config(cls, config):
        context = cls()
        config = json.loads(config)

        version = config.get('version', V_0_1)

        if V_0_1 == version:
            context = cls.parse_v01(context, config)
        elif V_0_2 == version:
            context = cls.parse_v02(context, config)
        else:
            raise Exception(f"Invalid context version -- '{version}' is not supported")

        # Ensure the wallet exists
        await try_to_create_wallet(context.wallet_config, context.wallet_credentials)

        context.wallet_handle = await wallet.open_wallet(
            context.wallet_config,
            context.wallet_credentials
        )

        context.wallet_closed = False

        return context

    @classmethod
    def parse_v01(cls, context, config):
        context.wallet_name = config['walletName']
        context.wallet_key = config['walletKey']
        context.wallet_path = config.get('walletPath')
        context.verity_url = config['verityUrl']
        context.verity_public_did = config.get('verityPublicDID')
        context.verity_public_verkey = config.get('verityPublicVerkey')
        context.domain_did = config.get('verityPairwiseDID')
        context.verity_agent_verkey = config.get('verityPairwiseVerkey')
        context.sdk_verkey_id = config.get('sdkPairwiseDID')
        context.sdk_verkey = config.get('sdkPairwiseVerkey')
        context.endpoint_url = config.get('endpointUrl')

        context.set_wallet_config(context.wallet_name, context.wallet_path)
        context.set_wallet_credentials(context.wallet_key)

        return context

    @classmethod
    def parse_v02(cls, context, config):
        context.wallet_name = config['walletName']
        context.wallet_key = config['walletKey']
        context.wallet_path = config.get('walletPath')
        context.verity_url = config['verityUrl']

        context.verity_public_did = config.get('verityPublicDID')
        context.verity_public_verkey = config.get('verityPublicVerKey')

        context.domain_did = config.get('domainDID')
        context.verity_agent_verkey = config.get('verityAgentVerKey')

        context.sdk_verkey_id = config.get('sdkVerKeyId')
        context.sdk_verkey = config.get('sdkVerKey')

        context.endpoint_url = config.get('endpointUrl')

        context.set_wallet_config(context.wallet_name, context.wallet_path)
        context.set_wallet_credentials(context.wallet_key)

        return context

    async def close_wallet(self):
        await wallet.close_wallet(self.wallet_handle)
        self.wallet_closed = True

    def to_json(self, indent=None) -> str:
        return json.dumps(
            {
                'walletName': self.wallet_name,
                'walletKey': self.wallet_key,
                'walletPath': self.wallet_path if hasattr(self, 'wallet_path') else None,
                'verityUrl': self.verity_url,
                'verityPublicDID': self.verity_public_did,
                'verityPublicVerKey': self.verity_public_verkey,
                'domainDID': self.domain_did,
                'verityAgentVerKey': self.verity_agent_verkey,
                'sdkVerKeyId': self.sdk_verkey_id,
                'sdkVerKey': self.sdk_verkey,
                'endpointUrl': self.endpoint_url,
                'version': V_0_2,
            },
            indent=indent
        )
