import requests
from src.utils.Did import Did


def retrieve_verity_public_did(verity_url) -> Did:
    full_url = f"{verity_url}/agency"
    result = requests.get(full_url)
    status_code = result.status_code
    if status_code > 399:
        raise IOError(f"Request failed! - {status_code} - {result.text}")
    else:
        try:
            msg = result.json()
            return Did(msg.get("DID"), msg.get("verKey"))
        except ValueError as e:
            raise IOError(f"Invalid and unexpected data from Verity -- response -- {e}")
