using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using System.Security.Policy;
using VeritySDK.Protocols.Relationship;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class RelationshipTest
    {
        string label = "Alice";
        Url logoUrl = new Url("http://server.com/profile_url.png");
        string phoneNumber = "+18011234567";
        string forRelationship = "did1";
        bool shortInvite = true;

        public RelationshipTest()
        {
        }

        [TestMethod]
        public void testGetMessageType()
        {
            RelationshipV1_0 relationshipProvisioning = Relationship.v1_0(
                    "forRelationship",
                    "threadId"
            );
            string msgName = "msg name";
            Assert.AreEqual(
                Util.getMessageType(
                        Util.EVERNYM_MSG_QUALIFIER,
                        "relationship",
                        "1.0",
                        msgName
                ),
                relationshipProvisioning.messageType(msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            RelationshipV1_0 testProtocol = Relationship.v1_0(label);
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testCreateMsg()
        {
            RelationshipV1_0 relationship = Relationship.v1_0();
            JsonObject msg = relationship.createMsg(TestHelpers.getContext());
            testCreateMsg(msg, false, false);

            RelationshipV1_0 relationship1 = Relationship.v1_0(label);
            JsonObject msg1 = relationship1.createMsg(TestHelpers.getContext());
            testCreateMsg(msg1, true, false);

            RelationshipV1_0 relationship2 = Relationship.v1_0(null, logoUrl);
            JsonObject msg2 = relationship2.createMsg(TestHelpers.getContext());
            testCreateMsg(msg2, false, true);

            RelationshipV1_0 relationship3 = Relationship.v1_0(label, logoUrl);
            JsonObject msg3 = relationship3.createMsg(TestHelpers.getContext());
            testCreateMsg(msg3, true, true);
        }

        private void testCreateMsg(JsonObject msg, bool hasLabel, bool hasLogo)
        {

            testBaseMessage(msg);
            Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/create", msg.getAsString("@type"));

            if (hasLabel)
                Assert.AreEqual(label.ToString(), msg.getAsString("label"));
            else
                Assert.IsFalse(msg.ContainsKey("label"));
            
            if (hasLogo)
                Assert.AreEqual(logoUrl.ToString(), msg.getAsString("logoUrl"));
            else
                Assert.IsFalse(msg.ContainsKey("logoUrl"));
        }

        [TestMethod]
        public void testConnectionInvitationMsg()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.connectionInvitationMsg(TestHelpers.getContext());
            testConnectionInvitationMsg(msg, false);
        }

        [TestMethod]
        public void testConnectionInvitationMsgWithShortInvite()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.connectionInvitationMsg(TestHelpers.getContext(), shortInvite);
            testConnectionInvitationMsg(msg, true);
        }

        private void testConnectionInvitationMsg(JsonObject msg, bool hasShortInvite)
        {
            testBaseMessage(msg);
            Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/connection-invitation", msg.getAsString("@type"));
            Assert.IsNotNull(forRelationship, msg.getAsString("~for_relationship"));
            if (hasShortInvite)
                Assert.AreEqual(shortInvite, msg.getAsBoolean("shortInvite"));
        }

        [TestMethod]
        public void testSMSConnectionInvitationMsg()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.smsConnectionInvitationMsg(TestHelpers.getContext(), phoneNumber);
            testSMSConnectionInvitationMsg(msg);
        }

        private void testSMSConnectionInvitationMsg(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/sms-connection-invitation", msg.getAsString("@type"));
            Assert.IsNotNull(forRelationship, msg.getAsString("~for_relationship"));
            Assert.AreEqual(phoneNumber, msg.getAsString("phoneNumber"));
        }

        [TestMethod]
        public void testOutOfBandIInvitationMsg()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.outOfBandInvitationMsg(TestHelpers.getContext());
            testOutOfBandInvitationMsg(msg, false, null);
        }

        [TestMethod]
        public void testOutOfBandIInvitationMsgWithShortInvite()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.outOfBandInvitationMsg(TestHelpers.getContext(), shortInvite);
            testOutOfBandInvitationMsg(msg, true, null);
        }

        [TestMethod]
        public void testOutOfBandIInvitationMsgWithGoalCode()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.outOfBandInvitationMsg(
                    TestHelpers.getContext(),
                    null,
                    GoalCode.REQUEST_PROOF
            );
            testOutOfBandInvitationMsg(msg, false, GoalCode.REQUEST_PROOF);
        }

        private void testOutOfBandInvitationMsg(JsonObject msg, bool hasShortInvite, GoalCode? goal)
        {
            testBaseMessage(msg);
            Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/out-of-band-invitation", msg.getAsString("@type"));
            if (goal != null)
            {
                Assert.IsTrue(msg.getAsString("goalCode").Equals(goal?.code()));
                Assert.IsTrue(msg.getAsString("goal").Equals(goal?.goalName()));
            }
            else
            {
                Assert.IsFalse(msg.ContainsKey("goalCode"));
                Assert.IsFalse(msg.ContainsKey("goal"));
            }
            if (hasShortInvite)
                Assert.AreEqual(shortInvite, msg.getAsBoolean("shortInvite"));
        }

        [TestMethod]
        public void testSMSOutOfBandIInvitationMsg()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.smsOutOfBandInvitationMsg(TestHelpers.getContext(), phoneNumber);
            testSMSOutOfBandInvitationMsg(msg, null);
        }

        [TestMethod]
        public void testSMSOutOfBandIInvitationMsgWithGoalCode()
        {
            RelationshipV1_0 relationship = Relationship.v1_0(forRelationship, "thread-id");
            JsonObject msg = relationship.smsOutOfBandInvitationMsg(
                    TestHelpers.getContext(),
                    phoneNumber,
                    GoalCode.REQUEST_PROOF
            );
            testSMSOutOfBandInvitationMsg(msg, GoalCode.REQUEST_PROOF);
        }

        private void testSMSOutOfBandInvitationMsg(JsonObject msg, GoalCode? goal)
        {
            testBaseMessage(msg);
            Assert.AreEqual(Util.EVERNYM_MSG_QUALIFIER + "/relationship/1.0/sms-out-of-band-invitation", msg.getAsString("@type"));
            Assert.AreEqual(phoneNumber, msg.getAsString("phoneNumber"));
            if (goal != null)
            {
                Assert.IsTrue(msg.getAsString("goalCode").Equals(goal?.code()));
                Assert.IsTrue(msg.getAsString("goal").Equals(goal?.goalName()));
            }
            else
            {
                Assert.IsFalse(msg.ContainsKey("goalCode"));
                Assert.IsFalse(msg.ContainsKey("goal"));
            }
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg.getAsString("@type"));
            Assert.IsNotNull(msg.getAsString("@id"));
            Assert.IsNotNull(msg.getAsJsonObject("~thread").getAsString("thid"));
        }
    }
}
