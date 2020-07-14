from urllib.parse import urljoin
import requests


def msg_endpoint(url: str) -> str:
    return urljoin(url, '/agency/msg')


def send_packed_message(context, message):
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
