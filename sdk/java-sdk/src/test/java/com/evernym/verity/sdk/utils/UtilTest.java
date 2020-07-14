package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestHelpers;
import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class UtilTest {

    @Test
    public void testPackMessageForVerityAndUnpackForward() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();

            JSONObject testMessage = new JSONObject().put("hello", "world");
            byte[] packedMessage = Util.packMessageForVerity(context, testMessage);

            JSONObject unpackedMessage = unpackForwardMessage(context, packedMessage);
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
        String msgType = "did:sov:123456789abcdefghi1234;spec/credential/0.1/status";
        assertEquals(msgType, Util.getMessageType(Util.EVERNYM_MSG_QUALIFIER, "credential", "0.1", "status"));
    }

    @Test
    public void noop() {
        new JSONObject("{}");
    }
}