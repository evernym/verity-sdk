import base64
import copy
import json

from indy.crypto import crypto_verify

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.transports import send_packed_message
from verity_sdk.utils import _pack_message_for_verity_direct, unpack_message, EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


class Provision(Protocol):
    """
    The Provision protocol is used to provision an agent on the verity-application. This agent is the element that
    represents an self-sovereign Identity (organization or otherwise). The protocol is unique that two ways. First it
    only run once per agent. So it is a one-time operation to start things. If run again, it will create a new and
    independent agent. Second, the interface for this protocol use an incomplete context object. It is during this
    protocol that the context object is fully filled out.
    """

    MSG_FAMILY = 'agent-provisioning'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '0.7'
    """the version for the message family"""

    # Messages
    CREATE_EDGE_AGENT = 'create-edge-agent'
    """Name for 'create-edge-agent' control message"""

    _token = None

    def __init__(self, token=None):
        """
        Args:
            token (str): an agent creation token that is provided by Evernym that authorize the creation of an agent
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )
        self._token = token

    # noinspection PyMethodMayBeStatic
    async def _send_to_verity(self, context, packed_msg) -> dict:
        resp_bytes = send_packed_message(context, packed_msg)

        return await unpack_message(context, resp_bytes)

    @staticmethod
    async def validate_token(token: str):
        """
        Checks the token for valid structure and signature. No explicit return but will throw exception if invalid.

        Args:
            token (str): an agent creation token that is validated
        """
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

    async def provision(self, context: Context) -> Context:
        """
        Sends provisioning message that directs the creation of an agent to the to verity-application

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            An updated Context object with details inserted from the provisioning process
        """
        if self._token is not None:
            await self.validate_token(self._token)

        msg = await self.provision_msg_packed(context)

        resp = await self._send_to_verity(context, msg)
        if resp['@type'] == self.message_type(self.PROBLEM_REPORT):
            raise Exception(resp['msg'])

        domain_did: str = resp['selfDID']
        verity_agent_verkey: str = resp['agentVerKey']

        new_context = copy.copy(context)
        new_context.domain_did = domain_did
        new_context.verity_agent_verkey = verity_agent_verkey
        return new_context

    async def provision_msg_packed(self, context: Context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await _pack_message_for_verity_direct(
            context.wallet_handle,
            self.provision_msg(context),
            context.verity_public_did,
            context.verity_public_verkey,
            context.sdk_verkey,
            context.verity_public_verkey
        )

    def provision_msg(self, context: Context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.CREATE_EDGE_AGENT)
        msg['requesterVk'] = context.sdk_verkey

        if self._token is not None:
            msg['provisionToken'] = json.loads(self._token)

        return msg
