package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SomeProtocol extends AbstractProtocol {
  private static final String MSG_QUALIFIER = "did:sov:some_did";
  private static final String MSG_FAMILY = "some-protocol";
  private static final String MSG_FAMILY_VERSION = "9.9.9";

  SomeProtocol() {
    super();
  }

  @Override
  public String qualifier() {
    return MSG_QUALIFIER;
  }

  @Override
  public String family() {
    return MSG_FAMILY;
  }

  @Override
  public String version() {
    return MSG_FAMILY_VERSION;
  }
}

public class AbstractProtocolTest {

  @Test
  public void testGetMessage() throws Exception {
    JSONObject message = new JSONObject("{\"hello\": \"world\"}");
    Context context = TestHelpers.getContext();
    byte[] packedMessage = AbstractProtocol.packMsg(context, message);
    JSONObject unpackedMessage = unpackForwardMessage(context, packedMessage);
    assertEquals(unpackedMessage.getString("hello"), "world");

    TestHelpers.cleanup(context);
  }
}