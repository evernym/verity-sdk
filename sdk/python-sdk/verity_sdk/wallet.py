import json


class WalletConfig:
    """
    The generic interface for the wallet configuration for the Indy-SDK wallet
    """

    def config(self) -> str:
        """
        Provides a JSON string that is valid config for the Indy-SDK wallet API
        Returns:
            a JSON wallet config string
        """

    def credential(self) -> str:
        """
        Provides a JSON string that is valid credential for the Indy-SDK wallet API
        Returns:
            a JSON wallet credential string
        """


def build(name, key, path=None):
    """
    Builds a DefaultWalletConfig from the given parameters

    Args:
        name (str): the given name for the wallet to be created
        key (str): the given key for the wallet to be created
        path (str): the given path where the wallet on disk file will be created
    Returns:
        DefaultWalletConfig: constructed with the given parameters
    """
    return DefaultWalletConfig(name, key, path)


class DefaultWalletConfig(WalletConfig):
    """
    The config object for the local default wallet provided by the Indy SDK
    """
    # Cannot use 'id' - shadows build-in name id
    name: str
    key: str
    path: str

    def __init__(self, name: str, key: str, path: str = None):
        """
        Args:
            name (str): the given name or identifier for the wallet
            key (str): the given key (encrypting) for the wallet
            path (str): the given path location where the wallet on disk file is found
        """
        self.name = name
        self.key = key
        self.path = path

    def config(self) -> str:
        """
        Provides a default JSON config string
        Returns:
            str: a JSON wallet config string
        """
        configuration = {
            'id': self.name
        }
        if self.path and self.path.strip:
            configuration['storage_config'] = {
                'path': self.path
            }
        return json.dumps(configuration)

    def credential(self) -> str:
        """
        Provides a default JSON credential string
        Returns:
            str: a JSON wallet credential string
        """
        return json.dumps({'key', self.key})

    def add_to_json(self, target_dict: dict) -> dict:
        """
        Injects the default wallet fields into a JSON Object
        Args:
            target_dict (dict): the JSON object that the fields are injected into
        """
        if self.name:
            target_dict['walletName'] = self.name
        if self.key:
            target_dict['walletKey'] = self.key
        if self.path:
            target_dict['walletPath'] = self.path
        return target_dict
