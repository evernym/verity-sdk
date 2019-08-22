package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestHelpers;

import org.json.JSONObject;
import org.junit.Test;

import static com.evernym.verity.sdk.utils.Util.truncateInviteDetails;
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
        String msgType = "did:sov:123456789abcdefghi1234;spec/credential/0.1/status";
        assertEquals(msgType, Util.getMessageType("credential", "0.1", "status"));
    }

    @Test
    public void testTruncateInviteDetails() {
        JSONObject inviteDetails = new JSONObject("{ \n" +
                "  \"connReqId\": \"NDRlYjF\",\n" +
                "  \"statusCode\": \"MS-101\",\n" +
                "  \"statusMsg\": \"message created\",\n" +
                "  \"targetName\": \"there\",\n" +
                "  \"version\": \"2.0\",\n" +
                "  \"threadId\": \"thread id\",\n" +
                "  \"senderDetail\": {\n" +
                "    \"DID\": \"2gZBfqEGmEoUTh6jew9aZB\",\n" +
                "    \"agentKeyDlgProof\": {\n" +
                "      \"agentDID\": \"6PYJhwRb9YAQ3s5eF56cXj\",\n" +
                "      \"agentDelegatedKey\": \"3wLzrdFwHmJEDp45HRtF75iJPh49p2vEF79oNzE66LPk\",\n" +
                "      \"signature\": \"1xYpvGFI9yK7yuC1AgM55NXWkXvfGyqyJa0pGr5ur1c7Qr00m2oS05MaPHknZaNkZMciyXoaKR1gUydeDhnODQ==\"\n" +
                "    },\n" +
                "    \"logoUrl\": \"https://i.postimg.cc/J0FWGN7r/Screen-Dev.png\",\n" +
                "    \"name\": \"Spencer Dev\",\n" +
                "    \"verKey\": \"vAuMGDTozuwSZjA3ZkV4JpXFFVEqSgkCwJbGiWzPERg\",\n" +
                "    \"publicDID\": \"DfEpJ4zmfQRE4PxyAFLzFf\"\n" +
                "  },\n" +
                "  \"senderAgencyDetail\": {\n" +
                "    \"DID\": \"CV65RFpeCtPu82hNF9i61G\",\n" +
                "    \"endpoint\": \"eas-team1.pdev.evernym.com:80/agency/msg\",\n" +
                "    \"verKey\": \"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"\n" +
                "  }\n" +
                "}\n");
        JSONObject truncatedInviteDetails = new JSONObject("{\n" +
                "  \"id\": \"NDRlYjF\",\n" +
                "  \"s\": {\n" +
                "    \"d\": \"2gZBfqEGmEoUTh6jew9aZB\",\n" +

                "    \"l\": \"https://i.postimg.cc/J0FWGN7r/Screen-Dev.png\",\n" +
                "    \"n\": \"Spencer Dev\",\n" +
                "    \"publicDID\": \"DfEpJ4zmfQRE4PxyAFLzFf\",\n" +
                "    \"v\": \"vAuMGDTozuwSZjA3ZkV4JpXFFVEqSgkCwJbGiWzPERg\",\n" +
                "    \"dp\": {\n" +
                "      \"d\": \"6PYJhwRb9YAQ3s5eF56cXj\",\n" +
                "      \"k\": \"3wLzrdFwHmJEDp45HRtF75iJPh49p2vEF79oNzE66LPk\",\n" +
                "      \"s\": \"1xYpvGFI9yK7yuC1AgM55NXWkXvfGyqyJa0pGr5ur1c7Qr00m2oS05MaPHknZaNkZMciyXoaKR1gUydeDhnODQ==\"\n" +
                "    },\n" +
                "  },\n" +
                "  \"sa\": {\n" +
                "    \"d\": \"CV65RFpeCtPu82hNF9i61G\",\n" +
                "    \"e\": \"eas-team1.pdev.evernym.com:80/agency/msg\",\n" +
                "    \"v\": \"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"\n" +
                "  },\n" +
                "  \"sc\": \"MS-101\",\n" +
                "  \"sm\": \"message created\",\n" +
                "  \"t\": \"there\",\n" +
                "  \"version\": \"2.0\",\n" +
                "  \"threadId\": \"thread id\"\n" +
                "}");

        assertEquals(truncatedInviteDetails.toString(), truncateInviteDetails(inviteDetails).toString());
        assertEquals(truncatedInviteDetails.toString(), truncateInviteDetails(inviteDetails.toString()).toString());
    }
}