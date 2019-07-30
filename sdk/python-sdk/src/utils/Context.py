import json

from indy import wallet

class Context:

  wallet_name: str
  wallet_key: str
  verity_url: str
  verity_public_verkey: str
  verity_pairwise_did: str
  verity_pairwise_verkey: str
  sdk_pairwise_verkey: str
  endpoint_url: str
  wallet_config: str
  wallet_credentials: str
  wallet_handle: int
  wallet_closed: bool

  @classmethod
  async def create(cls, config):
    context = cls()
    config = json.loads(config)

    context.wallet_name = config['walletName']
    context.wallet_key = config['walletKey']
    context.verity_url = config['verityUrl']
    context.verity_public_verkey = config['verityPublicVerkey']
    context.verity_pairwise_did = config['verityPairwiseDID']
    context.verity_pairwise_verkey = config['verityPairwiseVerkey']
    context.sdk_pairwise_verkey = config['sdkPairwiseVerkey']
    context.endpoint_url = config['endpointUrl']
    context.wallet_config = json.dumps({'id': config['walletName']})
    context.wallet_credentials = json.dumps({'key': config['walletKey']})
    context.wallet_handle = await wallet.open_wallet(context.wallet_config, context.wallet_credentials)
    context.wallet_closed = False

    return context

  async def close_wallet(self):
    await wallet.close_wallet(self.wallet_handle)
    self.wallet_closed = True
