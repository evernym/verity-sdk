from verity_sdk.utils import EVERNYM_MSG_QUALIFIER, uuid


class MessageFamily:
    """
    Interface for message families. Message families are the set of messages used by a protocol. They include
    three types of messages: protocol messages, control messages and signal messages.

    Protocol messages are messages exchange between parties of the protocol. Each party is an independent self-sovereign
    domain.

    Control messages are messages sent by a controller (applications that use verity-sdk are controllers) to the verity
    application. These messages control the protocol and make decisions for the protocol.

    Signal messages are messages sent from the verity-application agent to a controller

    Message family messages always have a type. This type has 4 parts: qualifier, family, version and name. Three parts
    are static and defined by this interface.

    Example: did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/connections/1.0/problem_report

    qualifier: did:sov:BzCbsNYhMrjHiqZDTUASHg

    family: connections

    version: 1.0

    name: problem_report
    """
    def __init__(self, msg_family: str, msg_family_version: str, msg_qualifier: str = EVERNYM_MSG_QUALIFIER):
        """
        Args:
            msg_family (str): the qualifier for the message family
            msg_family_version (str): the family name for the message family
            msg_qualifier (str): the version for the message family
        """
        self.msg_family = msg_family
        self.msg_family_version = msg_family_version
        self.msg_qualifier = msg_qualifier

    def _get_base_message(self, msg_name):
        return {
            '@id': uuid(),
            '@type': self.get_message_type(msg_name)
        }

    def message_type(self, msg_name):
        """
        Combines the element of this message family with the given message name to build a fully qualified message type

        Args:
            msg_name (str): the message name used in the built message type
        Returns:
            str: fully qualified message type
        """
        return '{};spec/{}/{}/{}'.format(self.msg_qualifier, self.msg_family, self.msg_family_version, msg_name)

    def get_message_type(self, msg_name):
        """
        Deprecated!
        See: message_type
        """
        return self.message_type(msg_name)
