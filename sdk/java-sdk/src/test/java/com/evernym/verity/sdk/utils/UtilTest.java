package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestHelpers;

import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class UtilTest {

    @Test
    public void testPackMessageForVerityAndUnpackForward() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();

            JSONObject testMessage = new JSONObject().put("hello", "world");
            byte[] packedMessage = Util.packMessageForVerity(context, testMessage);

            JSONObject unpackedMessage = Util.unpackForwardMessage(context, packedMessage);
            assertEquals(testMessage.toString(), unpackedMessage.toString());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testGetMessageTypeComplete() {
        String msgType = "did:sov:d8xBkXpPgvyR=d=xUzi42=PBbw;spec/credential/0.1/status";
        assertEquals(msgType, Util.getMessageType("credential", "0.1", "status"));
    }
}