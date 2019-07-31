import requests

def send_message(url: str, message: bytes):
  try:
    requests.post(url, data=message, headers={'Content-Type': 'application/octet-stream'})
  except requests.RequestException as e:
    print('ERROR: Failed to POST message to {}'.format(url))
    raise e
