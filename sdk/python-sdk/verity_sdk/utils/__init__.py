import json

from typing import Dict
from uuid import uuid4

from indy import crypto
from verity_sdk.utils.Context import Context

EVERNYM_MSG_QUALIFIER = 'did:sov:123456789abcdefghi1234'
"""QUALIFIER for evernym specific protocols"""
COMMUNITY_MSG_QUALIFIER = 'did:sov:BzCbsNYhMrjHiqZDTUASHg'
"""QUALIFIER for community specified protocol"""


def _prepare_forward_message(did: str, message: bytes) -> str:
    return json.dumps({
        '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
        '@fwd': did,
        '@msg': json.loads(message.decode('utf-8'))
    })


async def pack_message_for_verity(context: Context, message: dict) -> bytes:
    """
    Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
    public keys for encryption. The encryption and instructor is defined by the Aries community.

    Args:
        context (Context): an instance of the Context object initialized to a verity-application agent
        message (dict): the JSON message to be communicated to the verity-application
    Return:
        bytes: the byte array of the packaged and encrypted message
    """
    return await _pack_message_for_verity_direct(
        wallet_handle=context.wallet_handle,
        message=message,
        pairwise_remote_did=context.domain_did,
        pairwise_remote_verkey=context.verity_agent_verkey,
        pairwise_local_verkey=context.sdk_verkey,
        public_verkey=context.verity_public_verkey
    )


async def _pack_message_for_verity_direct(wallet_handle: int,
                                          message: dict,
                                          pairwise_remote_did: str,
                                          pairwise_remote_verkey: str,
                                          pairwise_local_verkey: str,
                                          public_verkey: str) -> bytes:
    # Special packaging for provisioning (for a message before the agent has been provisioned in the verity-application)
    # This should ONLY be needed for the provisioning message
    agent_message = await crypto.pack_message(
        wallet_handle,
        json.dumps(message),
        [pairwise_remote_verkey],
        pairwise_local_verkey,
    )
    forward_message = _prepare_forward_message(
        pairwise_remote_did,
        agent_message
    )
    return await crypto.pack_message(
        wallet_handle,
        forward_message,
        [public_verkey],
        None
    )


async def unpack_forward_message(context: Context, message: bytes) -> Dict:
    """
    Deprecated!
    """
    unpacked_once_message = await unpack_message(context, message)
    return await unpack_message(
        context,
        json.dumps(unpacked_once_message['@msg']).encode('utf-8')
    )


async def unpack_message(context: Context, message: bytes) -> Dict:
    """
    Extracts the message in the byte array that has been packaged and encrypted for a key that is locally held.
    Args:
        context (Context): an instance of the Context object initialized to a verity-application agent
        message (bytes): the raw message received from the verity-application agent
    Returns:
        dict: an unencrypted messages as a JSON object
    """
    jwe: bytes = await crypto.unpack_message(
        context.wallet_handle,
        message
    )
    message = json.loads(jwe.decode('utf-8'))['message']
    return json.loads(message)


def uuid() -> str:
    """
    Returns:
         str: a new random uuid
    """
    return str(uuid4())


# TODO: Remove this function in favor of using MessageFamily
def get_message_type(msg_family: str,
                     msg_family_version: str,
                     msg_name: str,
                     msg_qualifier=EVERNYM_MSG_QUALIFIER) -> str:
    """
    Deprecated!

    See: MesssageFamily.message_type
    """
    return '{};spec/{}/{}/{}'.format(msg_qualifier, msg_family, msg_family_version, msg_name)


def get_problem_report_message_type(msg_family: str, msg_family_version: str) -> str:
    """
    Deprecated!
    """
    return get_message_type(msg_family, msg_family_version, 'problem-report')


def get_status_message_type(msg_family: str, msg_family_version: str) -> str:
    """
    Deprecated!
    """
    return get_message_type(msg_family, msg_family_version, 'status')


class MsgType:
    def __init__(self, msgType: str):
        parts1 = msgType.split(';spec/')
        self.msg_qualifier = parts1[0]
        parts2 = parts1[1].split('/')
        self.msg_family = parts2[0]
        self.msg_family_version = parts2[1]
        self.msg_name = parts2[2]
