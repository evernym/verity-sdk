package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

class SomeProtocol extends Protocol {
  private static String MSG_FAMILY = "some-protocol";
  private static String MSG_FAMILY_VERSION = "9.9.9";

  SomeProtocol() {
    super(MSG_FAMILY, MSG_FAMILY_VERSION);
  }

  @Override
  protected void defineMessages() {

  }
}

public class ProtocolTest {

  @Test
  public void testGetMessage() throws Exception {
    JSONObject message = new JSONObject("{\"hello\": \"world\"}");
    Context context = TestHelpers.getContext();
    byte[] packedMessage = Protocol.getMessage(context, message);
    JSONObject unpackedMessage = Util.unpackForwardMessage(context, packedMessage);
    assertEquals(unpackedMessage.getString("hello"), "world");

    TestHelpers.cleanup(context);
  }

  @Test
  public void testGetMessageTypeComplete() {
    String msgType = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/status";
    assertEquals(msgType, Protocol.getMessageTypeComplete("credential", "0.1", "status"));
  }

  @Test
  public void testGetMessageType() {
    SomeProtocol someProtocol = new SomeProtocol();
    assertEquals(Protocol.getMessageTypeComplete("some-protocol", "9.9.9", "some-message"), someProtocol.getMessageType("some-message"));
  }

  @Test
  public void testGetProblemReportMessageType() {

  }

  @Test
  public void testGetStatusMessageType() {

  }
}