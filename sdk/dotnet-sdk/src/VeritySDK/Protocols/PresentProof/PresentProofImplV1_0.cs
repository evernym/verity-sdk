using System;
using System.Json;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     *
     * This is an implementation of PresentProofImplV1_0 but is not viable to user of Verity SDK. Created using the
     * static PresentProof class
     */

    public class PresentProofImplV1_0 : PresentProofV1_0
    {

        string PROOF_REQUEST = "request";
        string STATUS = "status";
        string REJECT = "reject";

        // flag if this instance started the interaction
        bool created = false;

        string forRelationship;
        string name;
        Attribute[]
        proofAttrs;
        Predicate[]
        proofPredicates;

        public PresentProofImplV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        public PresentProofImplV1_0(string forRelationship, string name, Attribute[] proofAttrs) : this(forRelationship, name, proofAttrs, null) { }

        public PresentProofImplV1_0(string forRelationship, string name, Predicate[] proofPredicates) : this(forRelationship, name, null, proofPredicates) { }

        public PresentProofImplV1_0(string forRelationship, string name, Attribute[] proofAttrs, Predicate[] proofPredicates)
        {
            this.forRelationship = forRelationship;
            this.name = name;
            this.proofAttrs = proofAttrs;
            this.proofPredicates = proofPredicates;
            this.created = true;
        }

        public override void request(Context context)
        {
            send(context, requestMsg(context));
        }

        public override JsonObject requestMsg(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to request presentation when NOT starting the interaction");
            }

            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(PROOF_REQUEST));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            msg.Add("name", this.name);
            msg.Add("proof_attrs", JsonUtil.makeArray(proofAttrs));
            if (this.proofPredicates != null)
                msg.Add("proof_predicates", JsonUtil.makeArray(proofPredicates));
            return msg;
        }

        public override byte[] requestMsgPacked(Context context)
        {
            return packMsg(context, requestMsg(context));
        }

        public override void accept(Context context)
        {
            throw new NotSupportedException();
        }

        public override JsonObject acceptMsg(Context context)
        {
            throw new NotSupportedException();
        }

        public override byte[] acceptMsgPacked(Context context)
        {
            throw new NotSupportedException();
        }

        public override void reject(Context context, string reason)
        {
            send(context, rejectMsg(context, reason));
        }

        public override JsonObject rejectMsg(Context context, string reason)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(REJECT));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            if (String.IsNullOrWhiteSpace(reason))
                msg.Add("reason", reason);
            return msg;
        }

        public override byte[] rejectMsgPacked(Context context, string reason)
        {
            return packMsg(context, rejectMsg(context, reason));
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
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            return msg;
        }

        public override byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }
    }
}