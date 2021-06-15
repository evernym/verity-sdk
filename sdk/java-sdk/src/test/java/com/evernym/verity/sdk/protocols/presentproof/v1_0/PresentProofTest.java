package com.evernym.verity.sdk.protocols.presentproof.v1_0;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.outofband.OutOfBand;
import com.evernym.verity.sdk.protocols.outofband.v1_0.OutOfBandV1_0;
import com.evernym.verity.sdk.protocols.presentproof.PresentProof;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.common.Restriction;
import com.evernym.verity.sdk.protocols.presentproof.common.RestrictionBuilder;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.jupiter.api.Assertions.*;

public class PresentProofTest {

    private final String forRelationship = "...someDid...";
    private final String proofRequestName = "Name Check";

    private final Restriction r1 = RestrictionBuilder
            .blank()
            .issuerDid("UOISDFOPUASOFIUSAF")
            .build();
    private final Attribute attr1 = PresentProofV1_0.attribute("age", r1);
    private final Predicate pred1 = new Predicate("age", 18, r1);
    private final Boolean byInvitation = true;

    @Test
    public void testGetMessageType() {
        PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, "");
        String msgName = "msg name";
        assertEquals(Util.getMessageType(
                Util.COMMUNITY_MSG_QUALIFIER,
                testProtocol.family(),
                testProtocol.version(),
                msgName
        ), testProtocol.messageType(msgName));
    }

    @Test
    public void testGetThreadId() {
        PresentProofV1_0 testProtocol = PresentProof.v1_0(
                forRelationship,
                proofRequestName,
                new Attribute[]{attr1},
                new Predicate[]{pred1}
        );
        assertNotNull(testProtocol.getThreadId());
    }

    @Test
    public void testConstructorWithAttr() throws VerityException {
        Context context = TestHelpers.getContext();
        PresentProofV1_0 testProtocol = PresentProof.v1_0(
                forRelationship,
                proofRequestName,
                new Attribute[]{attr1},
                new Predicate[]{pred1},
                byInvitation
        );

        JSONObject msg = testProtocol.requestMsg(context);
        testRequestMsgMessages(msg);

        JSONObject msg2 = testProtocol.statusMsg(context);
        testStatusMsg(msg2);
    }

    private void testRequestMsgMessages(JSONObject requestMsg) {
        testBaseMessage(requestMsg);
        assertEquals(
                Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/request",
                requestMsg.getString("@type")
        );
        assertEquals(forRelationship, requestMsg.getString("~for_relationship"));
        assertEquals(proofRequestName, requestMsg.getString("name"));
        assertEquals(attr1.toJson().toString(), requestMsg.getJSONArray("proof_attrs").get(0).toString());
        assertEquals(byInvitation, requestMsg.getBoolean("by_invitation"));
    }

    private void testStatusMsg(JSONObject statusMsg) {
        testBaseMessage(statusMsg);
        assertEquals(
                Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/status",
                statusMsg.getString("@type")
        );
        assertEquals(forRelationship, statusMsg.getString("~for_relationship"));
    }

    @Test
    public void testRequest() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProofV1_0 presentProof = PresentProof.v1_0(forRelationship, proofRequestName, attr1);
            byte [] message = presentProof.requestMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(
                    Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/request",
                    unpackedMessage.getString("@type")
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testGetStatus() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, UUID.randomUUID().toString());
            byte [] message = testProtocol.statusMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(
                    Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/status",
                    unpackedMessage.getString("@type")
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testAcceptProposal() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, UUID.randomUUID().toString());
            byte [] message = testProtocol.acceptProposalMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(
                    Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/accept-proposal",
                    unpackedMessage.getString("@type")
            );
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testReject() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, UUID.randomUUID().toString());
            byte [] message = testProtocol.rejectMsgPacked(context, "because");
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testBaseMessage(unpackedMessage);
            assertEquals(
                    Util.COMMUNITY_MSG_QUALIFIER + "/present-proof/1.0/reject",
                    unpackedMessage.getString("@type")
            );
            assertEquals("because", unpackedMessage.getString("reason"));
            assertFalse(testProtocol.rejectMsg(context, "").has("reason"));
            assertFalse(testProtocol.rejectMsg(context, null).has("reason"));
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private void testBaseMessage(JSONObject msg) {
        assertNotNull(msg.getString("@type"));
        assertNotNull(msg.getString("@id"));
        assertNotNull(msg.getJSONObject("~thread").getString("thid"));
    }
}