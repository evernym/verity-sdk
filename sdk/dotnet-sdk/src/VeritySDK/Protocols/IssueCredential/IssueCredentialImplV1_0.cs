using System;
using System.Collections.Generic;
using System.Json;
using System.Linq;
using System.Text;

namespace VeritySDK
{
    /// <summary>
    /// NON_VISIBLE
    /// 
    /// This is an implementation of IssueCredentialV1_0 but is not viable to user of Verity SDK.Created using the static IssueCredential class
    /// </summary>
    public class IssueCredentialImplV1_0 : IssueCredentialV1_0
    {
        // flag if this instance started the interaction
        bool created = false;

        string PROPOSE = "propose";
        string OFFER = "offer";
        string REQUEST = "request";
        string ISSUE = "issue";
        string REJECT = "reject";
        string STATUS = "status";

        string forRelationship;
        string credDefId;
        Dictionary<string, string> values;
        string comment;
        string price;
        bool autoIssue;

        public IssueCredentialImplV1_0(string forRelationship,
                            string credDefId,
                            Dictionary<string, string> values,
                            string comment,
                            string price,
                            bool autoIssue)
        {
            DbcUtil.requireNotNull(forRelationship, "forRelationship");
            DbcUtil.requireNotNull(credDefId, "credDefId");

            this.forRelationship = forRelationship;
            this.credDefId = credDefId;
            this.values = values;
            this.comment = comment;
            this.price = price;
            this.autoIssue = autoIssue;
            this.created = true;
        }

        public IssueCredentialImplV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        public override void proposeCredential(Context context)
        {
            //        send(context, proposeCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }


        public override JsonObject proposeCredentialMsg(Context context)
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

        public override byte[] proposeCredentialMsgPacked(Context context)
        {
            // return packMsg(context, proposeCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        public override void offerCredential(Context context)
        {
            send(context, offerCredentialMsg(context));
        }

        public override JsonObject offerCredentialMsg(Context context)
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

            return msg;
        }

        public override byte[] offerCredentialMsgPacked(Context context)
        {
            return packMsg(context, offerCredentialMsg(context));
        }

        public override void requestCredential(Context context)
        {
            //        send(context, requestCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        public override JsonObject requestCredentialMsg(Context context)
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

        public override byte[] requestCredentialMsgPacked(Context context)
        {
            //        return packMsg(context, requestCredentialMsg(context));
            throw new NotImplementedException("This API has not been implemented");
        }

        public override void issueCredential(Context context)
        {
            send(context, issueCredentialMsg(context));
        }

        public override JsonObject issueCredentialMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ISSUE));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("comment", comment);

            return msg;
        }

        public override byte[] issueCredentialMsgPacked(Context context)
        {
            return packMsg(context, issueCredentialMsg(context));
        }

        public override void reject(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to reject when NOT starting the interaction");
            }

            send(context, rejectMsg(context));
        }

        public override JsonObject rejectMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(REJECT));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("comment", comment);

            return msg;
        }

        public override byte[] rejectMsgPacked(Context context)
        {
            return packMsg(context, rejectMsg(context));
        }

        public override void status(Context context)
        {
            send(context, statusMsg(context));
        }

        public override JsonObject statusMsg(Context context)
        {

            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(STATUS));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);
            return msg;
        }

        public override byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}
