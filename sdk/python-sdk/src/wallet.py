import json


class WalletConfig:
    def config(self) -> str:
        pass

    def credential(self) -> str:
        pass


def build(self, name, key, path=None):
    return DefaultWalletConfig(name, key, path)


class DefaultWalletConfig(WalletConfig):
    # Cannot use 'id' - shadows build-in name id
    name: str
    key: str
    path: str

    def __init__(self, name, key, path=None):
        self.name = name
        self.key = key
        self.path = path

    def config(self) -> str:
        configuration = {
            "id": self.name
        }
        if self.path and self.path.strip:
            configuration['storage_config'] = {
                'path': self.path
            }
        return json.dumps(configuration)

    def credential(self) -> str:
        return json.dumps({"key", self.key})

    def add_to_json(self, target_dict: dict) -> dict:
        if self.name:
            target_dict["walletName"] = self.name
        if self.key:
            target_dict["walletKey"] = self.key
        if self.path:
            target_dict["walletPath"] = self.path
        return target_dict
