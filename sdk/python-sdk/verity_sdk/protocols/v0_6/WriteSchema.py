from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.DbcUtil import DbcUtil


class WriteSchema(Protocol):
    """
    The WriteCredentialDefinition protocol writes credential definitions to an Indy Identity Ledger
    (often the Sovrin Ledger). This protocol expect that the issuer has been setup.
    """

    MSG_FAMILY = 'write-schema'
    """the family name for the message family"""
    MSG_FAMILY_VERSION = '0.6'
    """the version for the message family"""

    # Messages
    WRITE_SCHEMA = 'write'
    """Name for 'write' control message"""

    STATUS = 'status-report'
    """Name for 'status-report' signal message"""

    def __init__(self, name: str, version: str, attrs: [str]):
        """
        Args:
            name (str): The given name for the schema
            version (str): The given version of the schema
            attrs (list[str]): A list of attribute name in the schema
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )

        DbcUtil.require_string_not_null_or_empty(name, 'name')
        DbcUtil.require_string_not_null_or_empty(version, 'version')
        DbcUtil.require_array_not_contain_null(attrs, 'attrs')

        self.name = name
        self.version = version
        self.attrs = attrs

    async def write(self, context):
        """
        Directs verity-application to write the specified Schema to the Ledger

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.write_msg_packed(context))

    def write_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.WRITE_SCHEMA)
        msg['name'] = self.name
        msg['version'] = self.version
        msg['attrNames'] = self.attrs
        self._add_thread(msg)
        return msg

    async def write_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.write_msg(context))
