from typing import List

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import Context, get_message_type, get_problem_report_message_type, get_status_message_type


class WriteSchema(Protocol):
    MSG_FAMILY = 'write-schema'
    MSG_FAMILY_VERSION = '0.6'

    # Messages
    WRITE_SCHEMA = 'write'
    STATUS = 'status-report'

    def __init__(self, name: str, version: str, attrs: [str]):
        super().__init__(self.MSG_FAMILY, self.MSG_FAMILY_VERSION)
        self.name = name
        self.version = version
        self.attrs = attrs

    async def write_msg(self, _):
        msg = self._get_base_message(self.WRITE_SCHEMA)
        msg['name'] = self.name
        msg['version'] = self.version
        msg['attrNames'] = self.attrs
        return msg

    async def write_msg_packed(self, context):
        return await self.get_message_bytes(context, await self.write_msg(context))

    async def write(self, context):
        await self.send_message(context, await self.write_msg_packed(context))