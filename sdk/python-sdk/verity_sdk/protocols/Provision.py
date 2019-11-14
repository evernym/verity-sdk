import copy

from verity_sdk.utils import Context, get_message_type, pack_message_for_verity_direct, unpack_message
from verity_sdk.transports import send_message
from verity_sdk.protocols.Protocol import Protocol


class Provision(Protocol):
  MSG_FAMILY = 'agent-provisioning'
  MSG_FAMILY_VERSION = '0.6'

  # Messages
  CREATE_AGENT = 'CREATE_AGENT'

  def __init__(self):
    pass

  def define_messages(self):
    raise AttributeError('Context is required to build messages')

  @staticmethod
  def get_message_type(msg_name: str) -> str:
    return get_message_type(Provision.MSG_FAMILY, Provision.MSG_FAMILY_VERSION, msg_name)

  @staticmethod
  def create_agent_msg(context: Context):
    return {
      '@id': Provision.get_new_id(),
      '@type': Provision.get_message_type(Provision.CREATE_AGENT),
      'fromDID': context.sdk_pairwise_did,
      'fromDIDVerKey': context.sdk_pairwise_verkey
    }

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
