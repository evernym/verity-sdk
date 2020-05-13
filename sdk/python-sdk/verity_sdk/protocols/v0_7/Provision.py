import copy

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.transports import send_packed_message
from verity_sdk.utils import Context, pack_message_for_verity_direct, unpack_message, EVERNYM_MSG_QUALIFIER


class Provision(Protocol):
    MSG_FAMILY = 'agent-provisioning'
    MSG_FAMILY_VERSION = '0.7'

    # Messages
    CREATE_AGENT = 'CREATE_AGENT'

    def __init__(self):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )

    async def sendToVerity(self, context: Context, packed_msg: bytes) -> bytes:
        resp_bytes = send_packed_message(context, packed_msg)

        await unpack_message(context, resp_bytes)

    async def provision_msg_packed(self, context: Context):
        return await pack_message_for_verity_direct(
            context.wallet_handle,
            self.provision_msg(context),
            context.verity_public_did,
            context.verity_public_verkey,
            context.sdk_verkey,
            context.verity_public_verkey
        )

    def provision_msg(self, context: Context):
        keys = {
            "fromVerKey": context.sdk_verkey
        }

        msg = self._get_base_message(self.CREATE_AGENT)
        msg['requesterKeys'] = keys

        return msg

    async def provision(self, context: Context) -> Context:
        msg = await self.provision_msg_packed(context)

        resp = self.sendToVerity(context, msg)

        domain_did: str = resp['selfDID']
        verity_agent_verkey: str = resp['agentVerKey']

        new_context = copy.copy(context)
        new_context.domain_did = domain_did
        new_context.verity_agent_verkey = verity_agent_verkey
        return new_context
