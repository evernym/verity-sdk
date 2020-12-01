from verity_sdk.protocols.Protocol import Protocol
from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
from verity_sdk.utils.Exeptions import WrongSetupException


class PresentProof(Protocol):
    """
    The PresentProof protocol allows one self-sovereign party ask another self-sovereign party for a private
    and verifiable presentation from credentials they hold. This request can be restricted to certain selectable
    restrictions.
    """
    MSG_FAMILY = 'present-proof'
    """the family name for the message family"""
    MSG_FAMILY_VERSION = '1.0'
    """the version for the message family"""

    PROOF_REQUEST = 'request'
    """Name for 'request' control message"""
    STATUS = 'status'
    """Name for 'status' control message"""
    REJECT = 'reject'
    """Name for 'reject' control message"""
    ACCEPT_PROPOSAL = 'accept-proposal'
    """Name for 'accept-proposal' control message"""
    PRESENTATION_RESULT = 'presentation-result'
    """Name for 'presentation-result' signal message"""
    PROTOCOL_INVITATION = 'protocol-invitation'
    """Name for 'protocol-invitation' signal message"""


    def __init__(self,
                 for_relationship: str,
                 thread_id: str = None,
                 name: str = None,
                 proof_attrs=None,
                 proof_predicates=None,
                 by_invitation: bool = False):
        """
        Args:
            for_relationship (str): the relationship identifier (DID) for the pairwise relationship that will be used
            thread_id (str): the thread id of the already started protocol
            name (str): a human readable name for the given request
            proof_attrs (List[Dict]): an array of attribute based restrictions
            proof_predicates (List[Dict]): an array of predicate based restrictions
            by_invitation (bool): flag to create out-of-band invitation as a part of the PresentProof protocol
        """
        super().__init__(
            self.MSG_FAMILY,
            self.MSG_FAMILY_VERSION,
            msg_qualifier=COMMUNITY_MSG_QUALIFIER,
            thread_id=thread_id
        )
        self.for_relationship = for_relationship
        self.name = name
        self.proof_attrs = proof_attrs
        self.proof_predicates = proof_predicates
        self.by_invitation = by_invitation
        self.created = thread_id is None

    async def request(self, context):
        """
        Directs verity-application to request a presentation of proof.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.request_msg_packed(context))

    def request_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        if not self.created:
            raise WrongSetupException('Unable to request presentation when NOT starting the interaction')
        msg = self._get_base_message(self.PROOF_REQUEST)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        msg['name'] = self.name
        msg['proof_attrs'] = self.proof_attrs
        if self.proof_predicates:
            msg['proof_predicates'] = self.proof_predicates
        msg['by_invitation'] = self.by_invitation
        return msg

    async def request_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.request_msg())

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

    async def accept_proposal(self, context):
        """
        Directs verity-application to accept the proposed presentation.
        verity application will send presentation request based on the proposal.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
        """
        await self.send_message(context, await self.accept_proposal_msg_packed(context))

    def accept_proposal_msg(self):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.ACCEPT_PROPOSAL)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        return msg

    async def accept_proposal_msg_packed(self, context):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.accept_proposal_msg())

    async def reject(self, context, reason):
        """
        Directs verity-application to reject this presentation proof protocol

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
            reason: the reason for rejection
        """
        await self.send_message(context, await self.reject_msg_packed(context, reason))

    def reject_msg(self, reason):
        """
        Creates the control message without packaging and sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
            reason: the reason for rejection

        Return:
            the constructed message (dict object)
        """
        msg = self._get_base_message(self.REJECT)
        self._add_thread(msg)
        self._add_relationship(msg, self.for_relationship)
        if reason:
            msg['reason'] = reason
        return msg

    async def reject_msg_packed(self, context, reason):
        """
        Creates and packages message without sending it.

        Args:
            context (Context): an instance of the Context object initialized to a verity-application agent
            reason: the reason for rejection

        Return:
            the bytes ready for transport
        """
        return await self.get_message_bytes(context, self.reject_msg(reason))
