import pytest

from test.test_utils import get_test_config
from verity_sdk.utils.Context import Context


@pytest.mark.asyncio
async def test_request():
    ctx = await Context.create_with_config(await get_test_config("000000000000000000000000Team1VAS"))
    t = await ctx.rest_api_token()
    assert t == "HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh:4Wf6JtGy9enwwXVKcUgADPq7Pnf9T2" \
                "YZ8LupMEVxcQQf98uuRYxWGHLAwXWp8DtaEYHo4cUeExDjApMfvLJQ48Kp"
