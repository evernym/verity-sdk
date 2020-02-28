import pytest

from src.protocols.WriteCredentialDefinition import WriteCredentialDefinition
from src.utils import unpack_forward_message, EVERNYM_MSG_QUALIFIER
from src.utils.Context import Context
from test.test_utils import get_test_config, send_stub, cleanup

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
    assert len(write_cred_def.messages) == 1


@pytest.mark.asyncio
async def test_write():
    context = await Context.create(await get_test_config())
    write_cred_def = WriteCredentialDefinition(name, schema_id, tag, revocation_details)
    write_cred_def.send = send_stub
    msg = await write_cred_def.write(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteCredentialDefinition.MSG_FAMILY,
        WriteCredentialDefinition.MSG_FAMILY_VERSION,
        WriteCredentialDefinition.WRITE_CRED_DEF
    )
    assert msg['@id'] is not None
    assert msg['name'] == name
    assert msg['schemaId'] == schema_id
    assert msg['tag'] == tag
    assert msg['revocationDetails'] == revocation_details

    await cleanup(context)
