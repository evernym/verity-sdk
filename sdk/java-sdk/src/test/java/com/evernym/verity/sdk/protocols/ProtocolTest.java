package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.assertEquals;

class SomeProtocol extends Protocol {
  private static final String MSG_FAMILY = "some-protocol";
  private static final String MSG_FAMILY_VERSION = "9.9.9";

  SomeProtocol() {
    super();
  }
}

public class ProtocolTest {

  @Test
  public void testGetMessage() throws Exception {
    JSONObject message = new JSONObject("{\"hello\": \"world\"}");
    Context context = TestHelpers.getContext();
    byte[] packedMessage = Protocol.packMsg(context, message);
    JSONObject unpackedMessage = unpackForwardMessage(context, packedMessage);
    assertEquals(unpackedMessage.getString("hello"), "world");

    TestHelpers.cleanup(context);
  }
}