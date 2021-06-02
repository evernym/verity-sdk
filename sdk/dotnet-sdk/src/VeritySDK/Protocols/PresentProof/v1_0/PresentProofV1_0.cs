using System;
using System.Json;
using VeritySDK.Protocols;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.PresentProof
{
    /// <summary>
    /// A class for controlling a 1.0 PresentProof protocol.
    /// </summary>
    public class PresentProofV1_0 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        public override string qualifier() { return Util.COMMUNITY_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "present-proof"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "1.0"; }

        #endregion 

        #region Constructors

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string name, Attribute[] proofAttrs) : this(forRelationship, name, proofAttrs, null) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string name, Predicate[] proofPredicates) : this(forRelationship, name, null, proofPredicates) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string name, Attribute[] proofAttrs, Predicate[] proofPredicates) : this(forRelationship, name, proofAttrs, proofPredicates, false) { }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string name, Attribute[] proofAttrs, Predicate[] proofPredicates, bool byInvitation)
        {
            this.forRelationship = forRelationship;
            this.name = name;
            this.proofAttrs = proofAttrs;
            this.proofPredicates = proofPredicates;
            this.byInvitation = byInvitation;
            this.created = true;
        }

        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0(string forRelationship, string threadId, ProposedAttribute[] attrs, ProposedPredicate[] predicates) : base(threadId) {
            this.forRelationship = forRelationship;
            this.proposedAttributes = attrs;
            this.proposedPredicates = predicates;
            this.created = true;
        }

        #endregion 

        string PROOF_REQUEST = "request";
        string STATUS = "status";
        string REJECT = "reject";
        string ACCEPT_PROPOSAL = "accept-proposal";
        // string PROTOCOL_INVITATION = "protocol-invitation";

        // flag if this instance started the interaction
        bool created = false;

        string forRelationship;
        string name;
        Attribute[] proofAttrs;
        Predicate[] proofPredicates;
        ProposedAttribute[] proposedAttributes;
        ProposedPredicate[] proposedPredicates;
        bool byInvitation;

        /// <summary>
        /// Creates an attribute restriction for a presentation of proof 
        /// </summary>
        /// <param name="name">the attribute name to apply the restriction</param>
        /// <param name="restrictions">an array restrictions to be used</param>
        /// <returns>An Attribute with the given name and restrictions</returns>
        public static Attribute attribute(string name, params Restriction[] restrictions)
        {
            return new Attribute(name, restrictions);
        }

        /// <summary>
        /// Creates a predicate restriction for a presentation of proof. Indy Anoncreds only supports, so the value should be expressed as a greater than predicate. 
        /// </summary>
        /// <param name="name">the attribute name to apply the restriction</param>
        /// <param name="name">(Optional) predicate type (">=", ">", "<=", "<") defaults to ">=" if not included</param>
        /// <param name="value">the value the attribute must be greater than</param>
        /// <param name="restrictions">an array restrictions to be used</param>
        /// <returns>A Predicate with the given name, value and restrictions</returns>
        public static Predicate predicate(string name,string type, int value, params Restriction[] restrictions)
        {
            return new Predicate(name, type, value, restrictions);
        }

        public static Predicate predicate(string name, int value, params Restriction[] restrictions)
        {
            return new Predicate(name, value, restrictions);
        }

        /// <summary>
        /// Directs verity-application to request a presentation of proof. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void request(Context context)
        {
            send(context, requestMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject requestMsg(Context context)
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
            msg.Add("by_invitation", this.byInvitation);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] requestMsgPacked(Context context)
        {
            return packMsg(context, requestMsg(context));
        }

        /// <summary>
        /// Directs verity-application to accept the request to present proof.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void accept(Context context)
        {
            throw new NotSupportedException();
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject acceptMsg(Context context)
        {
            throw new NotSupportedException();
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] acceptMsgPacked(Context context)
        {
            throw new NotSupportedException();
        }

        /// <summary>
        /// Directs verity-application to accept the proposal for present proof.
        /// Verity-application will send presentation request based on the proposal.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public void acceptProposal(Context context)
        {
            send(context, acceptProposalMsg(context));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject acceptProposalMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ACCEPT_PROPOSAL));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] acceptProposalMsgPacked(Context context)
        {
            return packMsg(context, acceptProposalMsg(context));
        }

        /// <summary>
        /// Directs verity-application to reject this presentation proof protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        public void reject(Context context, string reason)
        {
            send(context, rejectMsg(context, reason));
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject rejectMsg(Context context, string reason)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(REJECT));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
            if (!String.IsNullOrWhiteSpace(reason))
                msg.Add("reason", reason);
            return msg;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] rejectMsgPacked(Context context, string reason)
        {
            return packMsg(context, rejectMsg(context, reason));
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
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(STATUS));
            msg.Add("@id", getNewId());
            addThread(msg);
            msg.Add("~for_relationship", this.forRelationship);
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