import pytest

from test.test_utils import get_test_config, cleanup
from verity_sdk.protocols.v0_6.WriteCredentialDefinition import WriteCredentialDefinition
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context


name = 'name'
schema_id = 'schema id'
tag = 'tag'
revocation_details = {'support_revocation': False}


def test_init():
    write_cred_def = WriteCredentialDefinition(name, schema_id, tag, revocation_details)

    assert write_cred_def.name == name
    assert write_cred_def.schema_id == schema_id
    assert write_cred_def.tag == tag
    assert write_cred_def.revocation_details == revocation_details


@pytest.mark.asyncio
async def test_write():
    context = await Context.create_with_config(await get_test_config())
    write_cred_def = WriteCredentialDefinition(name, schema_id, tag, revocation_details)
    msg = write_cred_def.write_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteCredentialDefinition.MSG_FAMILY,
        WriteCredentialDefinition.MSG_FAMILY_VERSION,
        WriteCredentialDefinition.WRITE_CRED_DEF
    )
    assert msg['@id'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['name'] == name
    assert msg['schemaId'] == schema_id
    assert msg['tag'] == tag
    assert msg['revocationDetails'] == revocation_details

    await cleanup(context)
