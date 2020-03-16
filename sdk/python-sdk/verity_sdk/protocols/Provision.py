import copy

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.transports import send_message
from verity_sdk.utils import Context, pack_message_for_verity_direct, unpack_message

class Provision(Protocol):
    MSG_FAMILY = 'agent-provisioning'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    CREATE_AGENT = 'CREATE_AGENT'

    def __init__(self):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION)

    def create_agent_msg(self, context: Context):
        msg = self._get_base_message(self.CREATE_AGENT)
        msg['fromDID'] = context.sdk_pairwise_did
        msg['fromDIDVerKey'] = context.sdk_pairwise_verkey
        return msg

    async def provision_sdk(self, context: Context) -> Context:
        msg = await pack_message_for_verity_direct(
            context.wallet_handle,
            self.create_agent_msg(context),
            context.verity_public_did,
            context.verity_public_verkey,
            context.sdk_pairwise_verkey,
            context.verity_public_verkey
        )

        resp_bytes = send_message(context.verity_url, msg)

        resp = await unpack_message(context, resp_bytes)
        verity_pairwise_did: str = resp['withPairwiseDID']
        verity_pairwise_verkey: str = resp['withPairwiseDIDVerKey']

        new_context = copy.copy(context)
        new_context.verity_pairwise_did = verity_pairwise_did
        new_context.verity_pairwise_verkey = verity_pairwise_verkey
        return new_context
