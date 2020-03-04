import pytest

from verity_sdk.utils import unpack_forward_message, EVERNYM_MSG_QUALIFIER
from verity_sdk.utils.Context import Context
from verity_sdk.protocols.WriteSchema import WriteSchema
from ..test_utils import get_test_config, send_stub, cleanup

schema_name = 'schema name'
schema_version = '0.1.0'
attrs = ['name', 'degree', 'age']


def test_init():
    write_schema = WriteSchema(schema_name, schema_version, attrs)

    assert write_schema.name == schema_name
    assert write_schema.version == schema_version
    assert len(write_schema.attrs) == 3
    assert write_schema.attrs == list(attrs)


@pytest.mark.asyncio
async def test_write():
    context = await Context.create_with_config(await get_test_config())
    write_schema = WriteSchema(schema_name, schema_version, attrs)
    write_schema.send = send_stub
    msg = await write_schema.write(context)
    msg = await unpack_forward_message(context, msg)

    assert msg['@type'] == '{};spec/{}/{}/{}'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteSchema.MSG_FAMILY,
        WriteSchema.MSG_FAMILY_VERSION,
        WriteSchema.WRITE_SCHEMA
    )
    assert msg['@id'] is not None
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


def test_get_problem_report_message_type():
    write_schema = WriteSchema(schema_name, schema_version, attrs)
    assert write_schema.get_problem_report_message_type() == '{};spec/{}/{}/problem-report'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteSchema.MSG_FAMILY,
        WriteSchema.MSG_FAMILY_VERSION
    )


def test_get_status_message_type():
    write_schema = WriteSchema(schema_name, schema_version, attrs)
    assert write_schema.get_status_message_type() == '{};spec/{}/{}/status'.format(
        EVERNYM_MSG_QUALIFIER,
        WriteSchema.MSG_FAMILY,
        WriteSchema.MSG_FAMILY_VERSION,
    )
