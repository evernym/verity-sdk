from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER


class IssuerSetup(Protocol):
    """
    The IssuerSetup protocol starts the setup process for issuers of verifiable credentials.
    The main task is creating a public identity that can be used to issue credentials. This identity
    and it's keys are created and held on the verity-application. The public elements (DID and verkey)
    are communicated back via a signal message. The setup process encapsulated by this protocol must
    be completed before other issuing activities (write-schema, write-cred-def and issue-credential)
    can be done.
    """

    MSG_FAMILY = 'issuer-setup'
    """the family name for the message family"""

    MSG_FAMILY_VERSION = '0.6'
    """the version for the message family"""

    # Messages

    CREATE = 'create'
    """Name for 'create' control message"""

    CURRENT_PUBLIC_IDENTIFIER = 'current-public-identifier'
    """Name for 'current-public-identifier' control message"""

    PUBLIC_IDENTIFIER = 'public-identifier'
    """Name for 'public-identifier' signal message"""

    PUBLIC_IDENTIFIER_CREATED = 'public-identifier-created'
    """Name for 'public-identifier-created' signal message"""

    PROBLEM_REPORT = 'problem-report'

    def __init__(self):
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=EVERNYM_MSG_QUALIFIER
        )

    async def create(self, context):
        """
        Directs verity-application to start and create an issuer identity and set it up

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.create_msg_packed(context))

    def create_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.CREATE)
        self._add_thread(msg)
        return msg

    async def create_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.create_msg(context))

    async def current_public_identifier(self, context):
        """
        Asks the verity-application for the current issuer identity that is setup.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.current_public_identifier_msg_packed(context))

    def current_public_identifier_msg(self, context):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.CURRENT_PUBLIC_IDENTIFIER)
        self._add_thread(msg)
        return msg

    async def current_public_identifier_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.current_public_identifier_msg(context))
