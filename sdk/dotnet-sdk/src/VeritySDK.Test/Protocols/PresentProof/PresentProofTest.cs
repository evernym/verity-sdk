using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Json;
using VeritySDK.Protocols.PresentProof;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class PresentProofTest : TestBase
    {
        private string forRelationship = "...someDid...";
        private string proofRequestName = "Name Check";

        private static Restriction r1 = RestrictionBuilder
                                    .blank()
                                    .issuerDid("UOISDFOPUASOFIUSAF")
                                    .build();

        private Protocols.PresentProof.Attribute attr1 = PresentProofV1_0.attribute("age", r1);
        private Protocols.PresentProof.Predicate pred1 = new Predicate("age", 18, r1);
        private bool byInvitation = true;

        [TestMethod]
        public void testGetMessageType()
        {
            PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, "");
            string msgName = "msg name";
            Assert.AreEqual(Util.getMessageType(
                    Util.COMMUNITY_MSG_QUALIFIER,
                    testProtocol.family(),
                    testProtocol.version(),
                    msgName
            ), testProtocol.messageType(msgName));
        }

        [TestMethod]
        public void testGetThreadId()
        {
            PresentProofV1_0 testProtocol = PresentProof.v1_0(
                    forRelationship,
                    proofRequestName,
                    new Protocols.PresentProof.Attribute[] { attr1 },
                    new Protocols.PresentProof.Predicate[] { pred1 }
            );
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructorWithAttr()
        {
            Context context = TestHelpers.getContext();
            PresentProofV1_0 testProtocol = PresentProof.v1_0(
                    forRelationship,
                    proofRequestName,
                    new Protocols.PresentProof.Attribute[] { attr1 },
                    new Protocols.PresentProof.Predicate[] { pred1 },
                    byInvitation
            );

            JsonObject msg = testProtocol.requestMsg(context);
            testRequestMsgMessages(msg);

            JsonObject msg2 = testProtocol.statusMsg(context);
            testStatusMsg(msg2);
        }

        private void testRequestMsgMessages(JsonObject requestMsg)
        {
            testBaseMessage(requestMsg);
            Assert.AreEqual(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/request",
                    requestMsg.getAsString("@type")
            );
            Assert.AreEqual(forRelationship, requestMsg.getAsString("~for_relationship"));
            Assert.AreEqual(proofRequestName, requestMsg.getAsString("name"));
            Assert.AreEqual(attr1.toJson().ToString(), requestMsg.getAsJsonArray("proof_attrs")[0].ToString());
            Assert.AreEqual(byInvitation, requestMsg.getAsBoolean("by_invitation"));
        }

        private void testStatusMsg(JsonObject statusMsg)
        {
            testBaseMessage(statusMsg);
            Assert.AreEqual(
                    "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/status",
                    statusMsg.getAsString("@type"));
            Assert.AreEqual(forRelationship, statusMsg.getAsString("~for_relationship"));
        }

        [TestMethod]
        public void testRequest()
        {
            withContext(context => {
                PresentProofV1_0 presentProof = PresentProof.v1_0(forRelationship, proofRequestName, attr1);
                byte[] message = presentProof.requestMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/request",
                        unpackedMessage.getAsString("@type")
                );
            });
        }

        [TestMethod]
        public void testGetStatus()
        {
            withContext(context =>
            {
                PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, Guid.NewGuid().ToString());
                byte[] message = testProtocol.statusMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/status",
                        unpackedMessage.getAsString("@type")
                );
            });
        }

        [TestMethod]
        public void testReject()
        {
            withContext(context =>
            {
                PresentProofV1_0 testProtocol = PresentProof.v1_0(forRelationship, Guid.NewGuid().ToString());
                byte[] message = testProtocol.rejectMsgPacked(context, "because");
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, message);
                testBaseMessage(unpackedMessage);
                Assert.AreEqual(
                        "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/present-proof/1.0/reject",
                        unpackedMessage.getAsString("@type")
                );
                Assert.AreEqual("because", unpackedMessage.getAsString("reason"));
                Assert.IsFalse(testProtocol.rejectMsg(context, "").ContainsKey("reason"));
                Assert.IsFalse(testProtocol.rejectMsg(context, null).ContainsKey("reason"));
            });
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg.getAsString("@type"));
            Assert.IsNotNull(msg.getAsString("@id"));
            Assert.IsNotNull(msg.getAsJsonObject("~thread").getAsString("thid"));
        }
    }
}
