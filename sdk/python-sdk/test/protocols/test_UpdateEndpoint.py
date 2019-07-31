import pytest

from test.test_utils import get_test_config, send_stub, cleanup
from verity_sdk.protocols.UpdateEndpoint import UpdateEndpoint
from verity_sdk.utils import unpack_forward_message, MESSAGE_TYPE_DID
from verity_sdk.utils.Context import Context

@pytest.mark.asyncio
async def test_init():
  context = await Context.create(await get_test_config())
  update_endpoint = UpdateEndpoint(context)
  assert update_endpoint.context.endpoint_url == context.endpoint_url
  await cleanup(context)

@pytest.mark.asyncio
async def test_write():
  context = await Context.create(await get_test_config())
  update_endpoint = UpdateEndpoint(context)
  update_endpoint.send = send_stub
  msg = await update_endpoint.update()
  msg = await unpack_forward_message(context, msg)

  assert msg['@type'] == '{};spec/configs/0.6/UPDATE_COM_METHOD'.format(MESSAGE_TYPE_DID)
  assert msg['@id'] is not None
  assert msg['comMethod']['id'] == 'webhook'
  assert msg['comMethod']['type'] == 2
  assert msg['comMethod']['value'] == context.endpoint_url

  await cleanup(context)

def test_get_message_type():
  assert UpdateEndpoint.get_message_type('message_name') == '{};spec/configs/0.6/message_name'.format(MESSAGE_TYPE_DID)


def test_get_problem_report_message_type():
  assert UpdateEndpoint.get_problem_report_message_type() == '{};spec/configs/0.6/problem-report'.format(MESSAGE_TYPE_DID)


def test_get_status_message_type():
  assert UpdateEndpoint.get_status_message_type() == '{};spec/configs/0.6/status'.format(MESSAGE_TYPE_DID)
