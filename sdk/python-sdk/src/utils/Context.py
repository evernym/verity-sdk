import json

from indy import wallet

class Context:
  async def __init__(self, config):
    config = json.loads(config)
    self.wallet_name = config['walletName']
    self.wallet_key = config['walletKey']
    self.verity_url = config['verityUrl']
    self.verity_public_verkey = config['verityPublicVerkey']
    self.verity_pairwise_did = config['verityPairwiseDID']
    self.verity_pairwise_verkey = config['verityPairwiseVerkey']
    self.sdk_pairwise_verkey = config['sdkPairwiseVerkey']
    self.webhook_url = config['webhookUrl']
    wallet_config = json.dumps({'id': self.wallet_name})
    wallet_credentials = json.dumps({'key': self.wallet_key})
    self.wallet_handle = await wallet.open_wallet(wallet_config, wallet_credentials)
