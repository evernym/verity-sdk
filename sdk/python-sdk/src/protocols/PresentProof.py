from typing import List
from src.utils import Context
from src.protocols.Protocol import Protocol

class PresentProof(Protocol):
    MSG_FAMILY = 'present-proof'
    MSG_FAMILY_VERSION = '0.1'

    # Messages
    PROOF_REQUEST = 'request'

    class STATUS():
        PROOF_REQUEST_SENT = 0
        PROOF_RECEIVED = 1

    connection_id: str
    name: str
    proof_attrs: List[dict]
    revocation_interval: dict

    def __init__(self, connection_id: str, name: str, proof_attrs: List[dict], revocation_interval: dict = None):
        self.connection_id = connection_id
        self.name = name
        self.proof_attrs = proof_attrs
        self.revocation_interval = revocation_interval or {}
        self.define_messages()

    def define_messages(self):
        self.messages = {
            self.PROOF_REQUEST: {
                '@type': self.get_message_type(self.PROOF_REQUEST),
                '@id': self.get_new_id(),
                'connectionId': self.connection_id,
                'proofRequest': {
                    'name': self.name,
                    'proofAttrs': self.proof_attrs,
                    'revocationInterval': self.revocation_interval
                }
            }
        }

    async def request(self, context: Context) -> bytes:
        return await self.send(context, self.messages[self.PROOF_REQUEST])