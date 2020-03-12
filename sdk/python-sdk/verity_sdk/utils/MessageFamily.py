from verity_sdk.utils import EVERNYM_MSG_QUALIFIER, uuid


class MessageFamily:

    def __init__(self, msg_family, msg_family_version, msg_qualifier=EVERNYM_MSG_QUALIFIER):
        self.msg_family = msg_family
        self.msg_family_version = msg_family_version
        self.msg_qualifier = msg_qualifier

    def _get_base_message(self, msg_name):
        return {
            '@id': uuid(),
            '@type': self.get_message_type(msg_name)
        }

    def get_message_type(self, msg_name):
        return '{};spec/{}/{}/{}'.format(self.msg_qualifier, self.msg_family, self.msg_family_version, msg_name)
