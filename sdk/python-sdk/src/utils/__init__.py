import json
from typing import Dict
from uuid import uuid4
from indy import crypto

from src.utils import Context

EVERNYM_MSG_QUALIFIER = 'did:sov:123456789abcdefghi1234'
COMMUNITY_MSG_QUALIFIER = 'did:sov:BzCbsNYhMrjHiqZDTUASHg'


def prepare_forward_message(did: str, message: bytes) -> str:
    return json.dumps({
        '@type': 'did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD',
        '@fwd': did,
        '@msg': json.loads(message.decode('utf-8'))
    })


async def pack_message_for_verity(context: Context, message: dict) -> bytes:
    return await pack_message_for_verity_direct(
        wallet_handle=context.wallet_handle,
        message=message,
        pairwise_remote_did=context.verity_pairwise_did,
        pairwise_remote_verkey=context.verity_pairwise_verkey,
        pairwise_local_verkey=context.sdk_pairwise_verkey,
        public_verkey=context.verity_public_verkey
    )


async def pack_message_for_verity_direct(wallet_handle: int,
                                         message: dict,
                                         pairwise_remote_did: str,
                                         pairwise_remote_verkey: str,
                                         pairwise_local_verkey: str,
                                         public_verkey: str) -> bytes:
    agent_message = await crypto.pack_message(
        wallet_handle,
        json.dumps(message),
        [pairwise_remote_verkey],
        pairwise_local_verkey,
    )
    forward_message = prepare_forward_message(
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
    unpacked_once_message = await unpack_message(context, message)
    return await unpack_message(
        context,
        json.dumps(unpacked_once_message['@msg']).encode('utf-8')
    )


def truncate_invite_details(invite_details: dict):
    truncated_invite_details = {
        'sc': invite_details['statusCode'],
        'id': invite_details['connReqId'],
        'sm': invite_details['statusMsg'],
        't': invite_details['targetName'],
        'version': invite_details['version'],
        's': {
            'n': invite_details['senderDetail']['name'],
            'd': invite_details['senderDetail']['DID'],
            'l': invite_details['senderDetail']['logoUrl'],
            'v': invite_details['senderDetail']['verKey'],
            'dp': {
                'd': invite_details['senderDetail']['agentKeyDlgProof']['agentDID'],
                'k': invite_details['senderDetail']['agentKeyDlgProof']['agentDelegatedKey'],
                's': invite_details['senderDetail']['agentKeyDlgProof']['signature']
            },
        },
        'sa': {
            'd': invite_details['senderAgencyDetail']['DID'],
            'e': invite_details['senderAgencyDetail']['endpoint'],
            'v': invite_details['senderAgencyDetail']['verKey']
        }
    }

    if 'publicDID' in invite_details['senderDetail']:
        truncated_invite_details['s']['publicDID'] = invite_details['senderDetail']['publicDID']


async def unpack_message(context: Context, message: bytes) -> Dict:
    jwe: bytes = await crypto.unpack_message(
        context.wallet_handle,
        message
    )
    message = json.loads(jwe.decode('utf-8'))['message']
    return json.loads(message)


def uuid() -> str:
    return str(uuid4())


def get_message_type(msg_family: str,
                     msg_family_version: str,
                     msg_name: str,
                     msg_qualifier=EVERNYM_MSG_QUALIFIER) -> str:
    return '{};spec/{}/{}/{}'.format(msg_qualifier, msg_family, msg_family_version, msg_name)


def get_problem_report_message_type(msg_family: str, msg_family_version: str) -> str:
    return get_message_type(msg_family, msg_family_version, 'problem-report')


def get_status_message_type(msg_family: str, msg_family_version: str) -> str:
    return get_message_type(msg_family, msg_family_version, 'status')

class MsgType:
    def __init__(self, msgType: str):
        parts1 = msgType.split(';spec/')
        self.msg_qualifier = parts1[0]
        parts2 = parts1[1].split('/')
        self.msg_family = parts2[0]
        self.msg_family_version = parts2[1]
        self.msg_name = parts2[2]

