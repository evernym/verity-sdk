from urllib.parse import urljoin
import requests
from verity_sdk.utils.Context import Context


def send_message(url: str, message: bytes):
    try:
        return requests.post(
            msg_endpoint(url),
            data=message,
            headers={'Content-Type': 'application/octet-stream'}
        ).content
    except requests.RequestException as e:
        print('ERROR: Failed to POST message to {}'.format(url))
        raise e

def send_packed_message(context: Context, message: bytes):
    url = urljoin(context.verity_url, '/agency/msg')
    try:
        return requests.post(
            msg_endpoint(url),
            data=message,
            headers={'Content-Type': 'application/octet-stream'}
        ).content
    except requests.RequestException as e:
        print('ERROR: Failed to POST message to {}'.format(url))
        raise e


def msg_endpoint(url: str) -> str:
    return url + '/agency/msg'
