import json
import pytest

from src.utils import unpack_forward_message
from src.utils.Context import Context
from src.protocols.Protocol import Protocol
from ..test_utils import get_test_config, send_stub, cleanup


@pytest.mark.asyncio
async def test_get_message():
  message = {"hello": "world"}
  context = await Context.create(await get_test_config())
  packed_message = await Protocol.get_message(context, message)
  unpacked_message = json.dumps(await unpack_forward_message(context, packed_message))
  assert json.dumps(message) == unpacked_message
  await cleanup(context)


def test_get_message_type_complete():
  msg_type: str = 'did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/status'
  assert Protocol.get_message_type_complete("credential", "0.1", "status") == msg_type


def test_get_message_type():
  protocol = Protocol()
  assert protocol.get_message_type("message_name") == "{};spec/none/0.0.0/message_name".format(Protocol.MESSAGE_TYPE_DID)


def test_get_problem_report_message_type():
  protocol = Protocol()
  assert protocol.get_problem_report_message_type() == "{};spec/none/0.0.0/problem-report".format(Protocol.MESSAGE_TYPE_DID)


def test_get_status_message_type():
  protocol = Protocol()
  assert protocol.get_status_message_type() == "{};spec/none/0.0.0/status".format(Protocol.MESSAGE_TYPE_DID)