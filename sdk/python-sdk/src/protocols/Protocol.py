import json
from typing import Dict
from uuid import uuid4 as uuid

from src.utils import Context, pack_message_for_verity
from src.transports import send_message

class Protocol:

  @staticmethod
  def get_message(context: Context, message: Dict):
    return pack_message_for_verity(context, json.dumps(message))

  @staticmethod
  def send(context: Context, message: Dict):
    send_message(context.verity_url, message)
