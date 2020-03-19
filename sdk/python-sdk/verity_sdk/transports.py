from urllib.parse import urljoin
import requests
from verity_sdk.utils.Context import Context


def msg_endpoint(url: str) -> str:
    return urljoin(url, '/agency/msg')


def send_packed_message(context: Context, message: bytes):
    url = msg_endpoint(context.verity_url)
    try:
        return requests.post(
            url,
            data=message,
            headers={'Content-Type': 'application/octet-stream'}
        ).content
    except requests.RequestException as e:
        print('ERROR: Failed to POST message to {}'.format(url))
        raise e
