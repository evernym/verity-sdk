from urllib.parse import urljoin
import requests


def msg_endpoint(url: str) -> str:
    """
    Joins a given url to the message route on the verity-application

    Args:
        url: the given url to be jointed
    Returns:
        str: the full url to the message route
    """
    return urljoin(url, '/agency/msg')


def send_packed_message(context, packed_message: bytes):
    """
    Sends a packaged message to the verity-application.

    Args:
        context (Context): an instance of the Context object initialized to a verity-application agent
        packed_message (bytes): the message to send to Verity
    Returns:
        bytes: bytes returned from the verity-application
    """
    url = msg_endpoint(context.verity_url)
    try:
        return requests.post(
            url,
            data=packed_message,
            headers={'Content-Type': 'application/octet-stream'}
        ).content
    except requests.RequestException as e:
        print('ERROR: Failed to POST message to {}'.format(url))
        raise e
