import requests


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


def msg_endpoint(url: str) -> str:
    return url + '/agency/msg'
