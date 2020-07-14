import base64
import copy
import json

from indy.crypto import crypto_verify

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.transports import send_packed_message
from verity_sdk.utils import Context, pack_message_for_verity_direct, unpack_message, EVERNYM_MSG_QUALIFIER


class Provision(Protocol):
    """Provision 0.7 protocol interface class"""

    MSG_FAMILY = 'agent-provisioning'
    MSG_FAMILY_VERSION = '0.7'

    # Messages
    CREATE_EDGE_AGENT = 'create-edge-agent'

    token = None

    def __init__(self, token=None):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )
        self.token = token

    @staticmethod
    async def validate_token(token: str):
        tokenObj = json.loads(token)
        data = (
                tokenObj['nonce'] +
                tokenObj['timestamp'] +
                tokenObj['sponseeId'] +
                tokenObj['sponsorId']
        ).encode('utf-8')

        valid = await crypto_verify(
            tokenObj['sponsorVerKey'],
            data,
            base64.b64decode(tokenObj['sig'])
        )

        if not valid:
            raise Exception('Invalid provision token -- signature does not validate')

    async def send_to_verity(self, context, packed_msg) -> dict:
        resp_bytes = send_packed_message(context, packed_msg)

        return await unpack_message(context, resp_bytes)

    async def provision_msg_packed(self, context):
        return await pack_message_for_verity_direct(
            context.wallet_handle,
            self.provision_msg(context),
            context.verity_public_did,
            context.verity_public_verkey,
            context.sdk_verkey,
            context.verity_public_verkey
        )

    def provision_msg(self, context):
        msg = self._get_base_message(self.CREATE_EDGE_AGENT)
        msg['requesterVk'] = context.sdk_verkey

        if self.token is not None:
            msg['provisionToken'] = json.loads(self.token)

        return msg

    async def provision(self, context: Context) -> Context:
        if self.token is not None:
            await self.validate_token(self.token)

        msg = await self.provision_msg_packed(context)

        resp = await self.send_to_verity(context, msg)

        domain_did: str = resp['selfDID']
        verity_agent_verkey: str = resp['agentVerKey']

        new_context = copy.copy(context)
        new_context.domain_did = domain_did
        new_context.verity_agent_verkey = verity_agent_verkey
        return new_context
