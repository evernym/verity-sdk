from typing import Dict

from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Exeptions import WrongSetupException


class IssueCredential(Protocol):
    """
    The IssueCredential protocol allows one self-sovereign party to issue a verifiable credential to another
    self-sovereign party. On the verity-application, these protocols use `anoncreds` as defined in the
    Hyperledger-indy project. In the verity-sdk, the interface presented by these objects allow key-value attributes
    to be issued to parties that have a DID-comm channel.
    """
    MSG_FAMILY = 'issue-credential'
    """the family name for the message family"""
    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    PROPOSE = 'propose'
    """Name for 'propose' control message"""
    OFFER = 'offer'
    """Name for 'offer' control message"""
    REQUEST = 'request'
    """Name for 'request' control message"""
    ISSUE = 'issue'
    """Name for 'issue' control message"""
    REJECT = 'reject'
    """Name for 'reject' control message"""
    STATUS = 'status'
    """Name for 'status' control message"""

    SENT = 'sent'
    """Name for 'sent' signal message"""
    ACCEPT_REQUEST = 'accept-request'
    """Name for 'accept-request' signal message"""
    PROTOCOL_INVITATION = 'protocol-invitation'
    """Name for 'protocol-invitation' signal message"""

    #pylint: disable=too-many-arguments
    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 cred_def_id: str = None,
                 values: Dict[str, str] = None,
                 comment: str = None,
                 price: str = '0',
                 auto_issue: bool = False,
                 by_invitation: bool = False):
        """
        Args:
            for_relationship (str): the relationship identifier (DID) for the pairwise relationship that will be used
            thread_id (str): The thread id of the already started protocol
            cred_def_id (str): the Credential Definition that will be used to issue the credential
            values (Dict[str, str]): a map of key-value pairs that make up the attributes in the credential
            comment (str): a human readable comment that is presented before issuing the credential
            price (str): (possible future feature) can and should be left un-set
            auto_issue (bool): flag to automatically issue the credential after receiving response from the receiver (skip getting
                signal for the credential request and waiting for the issue control message)
            by_invitation (bool): flag to create out-of-band invitation as a part of the IssueCredential protocol

        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )

        self.created = thread_id is None
        self.for_relationship = for_relationship
        self.cred_def_id = cred_def_id
        self.values = values
        self.comment = comment
        self.price = '0'  # price should be 0 regardless of what is set (until we support price)
        self.auto_issue = auto_issue
        self.by_invitation = by_invitation

    async def propose_credential(self, context):
        """
        Directs verity-application to send credential proposal.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.propose_credential_msg_packed(context))

    def propose_credential_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        if not self.created:
            raise WrongSetupException('Unable to propose credentials when NOT starting the interaction')

        msg = self._get_base_message(self.PROPOSE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['credential_values'] = self.values
        msg['comment'] = self.comment
        return msg

    async def propose_credential_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.propose_credential_msg())

    async def offer_credential(self, context):
        """
        Directs verity-application to send credential offer.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.offer_credential_msg_packed(context))

    def offer_credential_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        if not self.created:
            raise WrongSetupException('Unable to offer credentials when NOT starting the interaction')

        msg = self._get_base_message(self.OFFER)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['credential_values'] = self.values
        msg['comment'] = self.comment
        msg['price'] = self.price
        msg['auto_issue'] = self.auto_issue
        msg['by_invitation'] = self.by_invitation
        return msg

    async def offer_credential_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.offer_credential_msg())

    async def request_credential(self, context):
        """
        Directs verity-application to send credential request.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.request_credential_msg_packed(context))

    def request_credential_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.REQUEST)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['cred_def_id'] = self.cred_def_id
        msg['comment'] = self.comment
        return msg

    async def request_credential_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.request_credential_msg())

    async def issue_credential(self, context):
        """
        Directs verity-application to issue credential and send it

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.issue_credential_msg_packed(context))

    def issue_credential_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.ISSUE)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['comment'] = self.comment
        return msg

    async def issue_credential_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.issue_credential_msg())

    async def reject(self, context):
        """
        Directs verity-application to reject the credential protocol

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.reject_msg_packed(context))

    def reject_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.REJECT)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['comment'] = self.comment
        return msg

    async def reject_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.reject_msg())

    async def status(self, context):
        """
        Ask for status from the verity-application agent

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.status_msg_packed(context))

    def status_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.STATUS)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def status_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.status_msg())
