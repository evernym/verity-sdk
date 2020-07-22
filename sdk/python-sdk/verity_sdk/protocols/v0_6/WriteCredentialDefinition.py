from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class WriteCredentialDefinition(Protocol):
    """
    The WriteCredentialDefinition protocol writes credential definitions to an Indy Identity Ledger (often the
    Sovrin Ledger). This protocol expect that the issuer has been setup."""

    MSG_FAMILY = 'write-cred-def'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '0.6'
    """the version for the message family"""

    # Messages
    WRITE_CRED_DEF = 'write'
    """Name for 'write' control message"""

    STATUS = 'status-report'
    """Name for 'status-report' signal message"""

    def __init__(self, name: str, schema_id: str, tag: str = None, revocation_details: dict = None):
        """
        Args:
            name (str): The name of the new credential definition
            schema_id (str): The id of the schema this credential definition will be based on
            tag (str): An optional tag for the credential definition
            revocation_details (dict): the revocation object defining revocation support
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER,
        )
        self.name = name
        self.schema_id = schema_id
        self.tag = tag
        self.revocation_details = revocation_details

    async def write(self, context):
        """
        Directs verity-application to write the specified Credential Definition to the Ledger

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
        msg = self._get_base_message(self.WRITE_CRED_DEF)
        msg['name'] = self.name
        msg['schemaId'] = self.schema_id
        msg['tag'] = self.tag
        msg['revocationDetails'] = self.revocation_details
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
