# import pytest
#
# from test.test_utils import get_test_config, cleanup
# from verity_sdk.protocols.v1_0.Connecting import Connecting
# from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
# from verity_sdk.utils.Context import Context
#
# source_id = '12345'
# phone_number = '1234357890'
# include_public_did = True
#
#
# def test_init():
#     connecting = Connecting(source_id, phone_number, include_public_did)
#
#     assert connecting.source_id == source_id
#     assert connecting.phone_number == phone_number
#     assert connecting.include_public_did == include_public_did
#
#
# @pytest.mark.asyncio
# async def test_connect():
#     context = await Context.create_with_config(await get_test_config())
#     connecting = Connecting(source_id, phone_number, include_public_did)
#     msg = connecting.connect_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         Connecting.MSG_FAMILY,
#         Connecting.MSG_FAMILY_VERSION,
#         Connecting.CREATE_CONNECTION
#     )
#     assert msg['@id'] is not None
#     assert msg['sourceId'] == source_id
#     assert msg['phoneNo'] == phone_number
#     assert msg['includePublicDID'] == include_public_did
#
#     await cleanup(context)
#
#
# @pytest.mark.asyncio
# async def test_status():
#     context = await Context.create_with_config(await get_test_config())
#     connecting = Connecting(source_id, phone_number, include_public_did)
#     msg = connecting.status_msg(context)
#
#     assert msg['@type'] == '{};spec/{}/{}/{}'.format(
#         EVERNYM_MSG_QUALIFIER,
#         Connecting.MSG_FAMILY,
#         Connecting.MSG_FAMILY_VERSION,
#         Connecting.GET_STATUS
#     )
#     assert msg['@id'] is not None
#     assert msg['sourceId'] == source_id
#
#     await cleanup(context)
