import json
from urllib.parse import urljoin

import requests
from base58 import b58encode

from indy import wallet, crypto

from verity_sdk.utils import Did
from verity_sdk.utils.Wallet import create_and_open_wallet

V_0_1 = '0.1'
V_0_2 = '0.2'


class Context:
    """
    This Context object holds the data for accessing an agent on a verity-application. A complete and correct data in
    the context allows for access and authentication to that agent.
    """
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
    # pylint: disable=too-many-arguments
    # pylint: disable=protected-access
    async def create(cls,
                     wallet_name: str,
                     wallet_key: str,
                     verity_url: str,
                     domain_did: str = None,
                     verity_agent_verkey: str = None,
                     endpoint_url: str = None,
                     seed: str = None,
                     wallet_path: str = None):
        """
        Creates a new Context with the given parameters.

        Args:
            wallet_name (str): the id for the local wallet
            wallet_key (str): the key (credential) for the local wallet
            verity_url (str): the url for the targeted instance of the verity-application
            domain_did (str): the domain DID for the agent already provisioned
            verity_agent_verkey (str): the verkey for the agent already provisioned
            endpoint_url (str): the given endpoint url
            seed (str): the seed used to generate the local key-pair
            wallet_path (str): the given path where the wallet on disk file will be created
        Returns:
            Context: constructed Context
        """
        context = cls()
        context._set_wallet_config(wallet_name, wallet_path)
        context._set_wallet_credentials(wallet_key)
        context.wallet_name = wallet_name
        context.wallet_key = wallet_key
        context.wallet_path = wallet_path
        context.verity_url = verity_url

        await context._update_verity_info()
        context.wallet_handle = await create_and_open_wallet(
            context.wallet_config,
            context.wallet_credentials
        )

        context.endpoint_url = endpoint_url

        context.domain_did = domain_did

        context.verity_agent_verkey = verity_agent_verkey

        sdk_key = await Did.create_new_did(context.wallet_handle, seed=seed)

        context.sdk_verkey_id = sdk_key.did
        context.sdk_verkey = sdk_key.verkey

        return context

    def _set_wallet_config(self, wallet_name, wallet_path=None):
        wallet_config = {'id': wallet_name}

        if wallet_path:
            wallet_config['storage_config'] = {'path': wallet_path}

        self.wallet_config = json.dumps(wallet_config)

    def _set_wallet_credentials(self, wallet_key):
        self.wallet_credentials = json.dumps({'key': wallet_key})

    async def _update_verity_info(self):
        full_url = urljoin(self.verity_url, 'agency')
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

    @classmethod
    async def create_with_config(cls, config: str):
        """
        Loads the fields contained in the given JSON Object into this ContextBuilder. The JSON Object should be
        from a Context object that was converted to JSON
        Args:
            config (str): a str that contains properly formatted JSON
        Returns:
            Context: this ContextBuilder with loaded fields
        """
        context = cls()
        config = json.loads(config)

        version = config.get('version', V_0_1)

        if V_0_1 == version:
            context = cls._parse_v01(context, config)
        elif V_0_2 == version:
            context = cls._parse_v02(context, config)
        else:
            raise Exception(f'Invalid context version -- "{version}" is not supported')

        context.wallet_handle = await wallet.open_wallet(
            context.wallet_config,
            context.wallet_credentials
        )

        context.wallet_closed = False

        return context

    @classmethod
    # pylint: disable=protected-access
    def _parse_v01(cls, context, config):
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

        context._set_wallet_config(context.wallet_name, context.wallet_path)
        context._set_wallet_credentials(context.wallet_key)

        return context

    @classmethod
    # pylint: disable=protected-access
    def _parse_v02(cls, context, config):
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

        context._set_wallet_config(context.wallet_name, context.wallet_path)
        context._set_wallet_credentials(context.wallet_key)

        return context

    async def close_wallet(self):
        """
        Closes the the open wallet handle stored inside the Context object.
        """
        await wallet.close_wallet(self.wallet_handle)
        self.wallet_closed = True

    def to_json(self, indent=None) -> str:
        """
        Converts this object into a JSON object
        Args:
            indent (int): number to indent for pretty printing
        Returns:
            str: a string (JSON encoded) based on this context
        """
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

    async def rest_api_token(self) -> str:
        """
        Converts the local keys held in the context to REST api token. This token can be used with the REST API for the
        Return:
            str: a REST API token
        """
        verkey = self.sdk_verkey
        b = await crypto.crypto_sign(self.wallet_handle, verkey, verkey.encode('utf-8'))
        t = b58encode(b).decode('utf-8')
        return verkey + ':' + t
