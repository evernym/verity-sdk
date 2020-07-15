# from typing import List
#
# from verity_sdk.protocols.v1_0.CommittedAnswer import CommittedAnswer
# from verity_sdk.utils import COMMUNITY_MSG_QUALIFIER
#
#
# class QuestionAnswer(CommittedAnswer):
#     MSG_FAMILY = 'questionanswer'
#     MSG_FAMILY_VERSION = '1.0'
#
#     def __init__(self,
#                  for_relationship: str,
#                  thread_id: str = None,
#                  question: str = None,
#                  descr: str = '',
#                  valid_responses: List[str] = None,
#                  signature_required: bool = True,
#                  answer_str=None):
#         super().__init__(for_relationship, thread_id, question, descr, valid_responses, signature_required, answer_str,
#                          self.MSG_FAMILY, self.MSG_FAMILY_VERSION, COMMUNITY_MSG_QUALIFIER)
