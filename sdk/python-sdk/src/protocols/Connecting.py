
from src.utils import Context
from src.protocols.Protocol import Protocol


class Connecting(Protocol):
    MSG_FAMILY = 'connecting'
    MSG_FAMILY_VERSION = '0.1'

    # Messages
    CREATE_CONNECTION = 'CREATE_CONNECTION'

    class STATUS():
        AWAITING_RESPONSE = 0
        INVITE_ACCEPTED = 1

    source_id: str
    phone_number: str
    use_public_did: bool

    def __init__(self, source_id: str, phone_number: str = None, use_public_did: bool = False):
        self.source_id = source_id
        self.phone_number = phone_number
        self.use_public_did = use_public_did

        self.define_messages()


    def define_messages(self):
        self.messages = {
            self.CREATE_CONNECTION: {
                '@type': self.get_message_type(self.CREATE_CONNECTION),
                '@id': self.get_new_id(),
                'connectionDetail': {
                    'sourceId': self.source_id,
                    'phoneNo': self.phone_number,
                    'usePublicDid': self.use_public_did
                }
            }
        }


    async def connect(self, context: Context) -> bytes:
        return await self.send(context, self.messages[self.CREATE_CONNECTION])
