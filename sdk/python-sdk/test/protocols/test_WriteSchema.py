import pytest
from verity_sdk.protocols.v0_6.WriteSchema import WriteSchema
from verity_sdk.utils import EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context

from ..test_utils import get_test_config, cleanup

schema_name = 'schema name'
schema_version = '0.1.0'
attrs = ['name', 'degree', 'age']


def test_init():
    write_schema = WriteSchema(schema_name, schema_version, attrs)

    assert write_schema.name == schema_name
    assert write_schema.version == schema_version
    assert len(write_schema.attrs) == 3
    assert write_schema.attrs == list(attrs)

    with pytest.raises(ValueError):
        WriteSchema(None, schema_version, attrs)
    with pytest.raises(ValueError):
        WriteSchema('', schema_version, attrs)
    with pytest.raises(ValueError):
        WriteSchema(schema_name, None, attrs)
    with pytest.raises(ValueError):
        WriteSchema(schema_name, '', attrs)
    with pytest.raises(ValueError):
        WriteSchema(schema_name, schema_version, None)
    with pytest.raises(ValueError):
        WriteSchema(schema_name, schema_version, ['name', None])


@pytest.mark.asyncio
async def test_write():
    context = await Context.create_with_config(await get_test_config())
    write_schema = WriteSchema(schema_name, schema_version, attrs)
    msg = write_schema.write_msg(context)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteSchema.MSG_FAMILY,
        WriteSchema.MSG_FAMILY_VERSION,
        WriteSchema.WRITE_SCHEMA
    )
    assert msg['@id'] is not None
    assert msg['~thread']['thid'] is not None
    assert msg['name'] == schema_name
    assert msg['version'] == schema_version
    assert msg['attrNames'] == list(attrs)

    await cleanup(context)


def test_get_message_type():
    write_schema = WriteSchema(schema_name, schema_version, attrs)
    assert write_schema.get_message_type('message_name') == '{};spec/{}/{}/message_name'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteSchema.MSG_FAMILY,
        WriteSchema.MSG_FAMILY_VERSION
    )
