using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using VeritySDK.Protocols.IssueCredential;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class IssueCredentialTest : TestBase
    {
        private string forRelationship = "...someDid...";
        private Dictionary<string, string> values = new Dictionary<string, string>();
        private string credDefId = "cred-def-id";
        private string comment = "some comment";
        private string price = "0";
        private bool autoIssue = true;
        private bool byInvitation = true;
        private string threadId = "some thread id";

        public IssueCredentialTest()
        {
            values.Add("name", "Jose Smith");
            values.Add("degree", "Bachelors");
            values.Add("gpa", "3.67");
        }

        [TestMethod]
        public void testGetMessageType()
        {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                    forRelationship,
                    threadId
            );
            string msgName = "msg name";
            Assert.AreEqual(
                    Util.getMessageType(Util.COMMUNITY_MSG_QUALIFIER, testProtocol.family(),
                            testProtocol.version(), msgName), testProtocol.messageType(msgName)
            );
        }

        [TestMethod]
        public void testGetThreadId()
        {
            IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(
                    forRelationship,
                    threadId
            );
            Assert.IsNotNull(testProtocol.getThreadId());
        }

        [TestMethod]
        public void testConstructorWithRequiredFieldAsNull()
        {
            Assert.ThrowsException<ArgumentException>(() =>
                IssueCredential.v1_0(null, null, null, null, null, null)
            );
        }

        [TestMethod]
        public void testConstructorWithAllOptionalAsNull()
        {
            Assert.ThrowsException<ArgumentException>(() =>
            IssueCredential.v1_0(forRelationship, null, null, null, null, null)
            );
        }

        [TestMethod]
        public void testSendOffer()
        {
            withContext(context =>
            {
                IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, credDefId, values, comment, price, autoIssue, byInvitation);
                byte[] msg = testProtocol.offerCredentialMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testOfferMessage(unpackedMessage);
            });
        }

        [TestMethod]
        public void testIssueCred()
        {
            withContext(context =>
            {
                IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
                byte[] msg = testProtocol.issueCredentialMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testIssueCredMessage(unpackedMessage);
            });
        }

        [TestMethod]
        public void testReject()
        {
            withContext(context =>
            {
                IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
                byte[] msg = testProtocol.rejectMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testRejectMessage(unpackedMessage);
            });
        }

        [TestMethod]
        public void testStatus()
        {
            withContext(context =>
            {
                IssueCredentialV1_0 testProtocol = IssueCredential.v1_0(forRelationship, threadId);
                byte[] msg = testProtocol.statusMsgPacked(context);
                JsonObject unpackedMessage = TestHelpers.unpackForwardMessage(context, msg);
                testStatusMessage(unpackedMessage);
            });
        }

        //below are helper methods

        private void testProposalMessage(JsonObject msg)
        {
            testCommonProposeAndOfferMsg(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/propose", msg.getAsString("@type"));
        }

        private void testOfferMessage(JsonObject msg)
        {
            testCommonProposeAndOfferMsg(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/offer", msg.getAsString("@type"));
            Assert.AreEqual(msg.getAsString("price"), price);
            Assert.AreEqual(bool.Parse(msg.getAsString("auto_issue")), autoIssue);
            Assert.AreEqual(bool.Parse(msg.getAsString("by_invitation")), byInvitation);
        }

        private void testIssueCredMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/issue", msg.getAsString("@type"));
            Assert.AreEqual(threadId, msg.getAsJsonObject("~thread").getAsString("thid"));
        }

        private void testRequestMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/request", msg["@type"].ToString().Trim('"'));
            Assert.AreEqual(credDefId, msg.getAsString("cred_def_id"));
        }

        private void testRejectMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/reject", msg["@type"].ToString().Trim('"'));
            Assert.AreEqual(threadId, msg.getAsJsonObject("~thread").getAsString("thid"));
        }

        private void testStatusMessage(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/issue-credential/1.0/status", msg["@type"].ToString().Trim('"'));
            Assert.AreEqual(threadId, msg.getAsJsonObject("~thread").getAsString("thid"));
        }

        private void testCommonProposeAndOfferMsg(JsonObject msg)
        {
            testBaseMessage(msg);
            Assert.AreEqual(credDefId, msg.getAsString("cred_def_id"));

            var val = new JsonObject();
            foreach (var v in values)
            {
                val.Add(v.Key, v.Value);
            };
            Assert.AreEqual(val.ToString(), msg.getAsString("credential_values"));
            Assert.AreEqual(comment, msg.getAsString("comment"));
        }

        private void testBaseMessage(JsonObject msg)
        {
            Assert.IsNotNull(msg["@type"]);
            Assert.IsNotNull(msg["@id"]);
            Assert.AreEqual(forRelationship, msg.getAsString("~for_relationship"));
            Assert.IsNotNull(msg["~thread"]["thid"]);
        }
    }
}