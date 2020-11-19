using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.IssueCredential
{
    /// <summary>
    /// A class  for controlling a 1.0 IssueCredential protocol. 
    /// </summary>
    public class IssueCredentialV1_0 : AbstractProtocol
    {

        #region Protocol identificator

        /// <summary>
        /// The qualifier for message family. Uses the community qualifier.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The name for message family.
        /// </summary>
        public override string family() { return "issue-credential"; }

        /// <summary>
        /// The version for message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion 

        // flag if this instance started the interaction
        bool created = false;

        //string PROPOSE = "propose";
        string OFFER = "offer";
        //string REQUEST = "request";
        string ISSUE = "issue";
        string REJECT = "reject";
        string STATUS = "status";
        //string PROTOCOL_INVITATION = "protocol-invitation";

        string forRelationship;
        string credDefId;
        Dictionary<string, string> values;
        string comment;
        string price;
        bool? autoIssue;
        bool byInvitation;

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public IssueCredentialV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public IssueCredentialV1_0(string threadId) : base(threadId) { }

        public IssueCredentialV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        public IssueCredentialV1_0(string forRelationship,
                            string credDefId,
                            Dictionary<string, string> values,
                            string comment,
                            string price,
                            bool? autoIssue,
                            bool byInvitation)
        {
            DbcUtil.requireNotNull(forRelationship, "forRelationship");
            DbcUtil.requireNotNull(credDefId, "credDefId");

            this.forRelationship = forRelationship;
            this.credDefId = credDefId;
            this.values = values;
            this.comment = comment;
            this.price = price;
            this.autoIssue = autoIssue;
            this.byInvitation = byInvitation;
            this.created = true;
        }

        public IssueCredentialV1_0(string forRelationship,
                            string credDefId,
                            Dictionary<string, string> values,
                            string comment,
                            string price,
                            bool? autoIssue) : this(forRelationship, credDefId, values, comment, price, autoIssue, false) { }

        #endregion 

        /// <summary>
        /// Directs verity-application to send credential proposal. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void proposeCredential(Context context)
        {
            //        send(context, proposeCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject proposeCredentialMsg(Context context)
        {
            //        if(!created) {
            //            throw new Exception("Unable to propose credentials when NOT starting the interaction");
            //        }
            //
            //        JsonObject msg = new JsonObject();
            //        msg.Add("@type", messageType(PROPOSE));
            //        msg.Add("@id", getNewId());
            //        msg.Add("~for_relationship", forRelationship);
            //        addThread(msg);
            //
            //        msg.Add("cred_def_id", credDefId);
            //        msg.Add("credential_values", values);
            //        msg.Add("comment", comment);
            //
            //        return msg;
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] proposeCredentialMsgPacked(Context context)
        {
            // return packMsg(context, proposeCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Directs verity-application to send credential offer.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void offerCredential(Context context)
        {
            send(context, offerCredentialMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message(JSON object)</returns>
        public JsonObject offerCredentialMsg(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to offer credentials when NOT starting the interaction");
            }

            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(OFFER));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("cred_def_id", credDefId);

            var val = new JsonObject();
            foreach (var v in this.values)
            {
                val.Add(v.Key, v.Value);
            }
            msg.Add("credential_values", val);

            msg.Add("comment", comment);
            msg.Add("price", price);
            msg.Add("auto_issue", autoIssue);
            msg.Add("by_invitation", byInvitation);

            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] offerCredentialMsgPacked(Context context)
        {
            return packMsg(context, offerCredentialMsg(context));
        }

        /// <summary>
        /// Directs verity-application to send credential request. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void requestCredential(Context context)
        {
            //        send(context, requestCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject requestCredentialMsg(Context context)
        {
            //        if(!created) {
            //            throw new Exception("Unable to request credential when NOT starting the interaction");
            //        }
            //
            //        JsonObject msg = new JsonObject();
            //        msg.Add("@type", messageType(REQUEST));
            //        msg.Add("@id", getNewId());
            //        msg.Add("~for_relationship", forRelationship);
            //        addThread(msg);
            //
            //        msg.Add("cred_def_id", credDefId);
            //        msg.Add("comment", comment);
            //
            //        return msg;
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] requestCredentialMsgPacked(Context context)
        {
            //        return packMsg(context, requestCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        /// <summary>
        /// Directs verity-application to issue credential and send it 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void issueCredential(Context context)
        {
            send(context, issueCredentialMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject issueCredentialMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ISSUE));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("comment", comment);

            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] issueCredentialMsgPacked(Context context)
        {
            return packMsg(context, issueCredentialMsg(context));
        }

        /// <summary>
        /// Directs verity-application to reject the credential protocol
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
        public void reject(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to reject when NOT starting the interaction");
            }

            send(context, rejectMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject rejectMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(REJECT));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("comment", comment);

            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] rejectMsgPacked(Context context)
        {
            return packMsg(context, rejectMsg(context));
        }

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void status(Context context)
        {
            send(context, statusMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject statusMsg(Context context)
        {

            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(STATUS));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}
