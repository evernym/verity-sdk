from verity_sdk.protocols.Protocol import Protocol


class WriteCredentialDefinition(Protocol):
    MSG_FAMILY = 'write-cred-def'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    WRITE_CRED_DEF = 'write'
    STATUS = 'status-report'

    def __init__(self, name: str, schema_id: str, tag: str = None, revocation_details: dict = None):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION)
        self.name = name
        self.schema_id = schema_id
        self.tag = tag
        self.revocation_details = revocation_details

    async def write_msg(self, _):
        msg = self._get_base_message(self.WRITE_CRED_DEF)
        msg['name'] = self.name
        msg['schemaId'] = self.schema_id
        msg['tag'] = self.tag
        msg['revocationDetails'] = self.revocation_details
        return msg

    async def write_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.write_msg(context))

    async def write(self, context):
        await self.send_message(context, await self.write_msg_packed(context))
