using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// An interface for controlling a 1.0 PresentProof protocol.
    /// <see cref="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof"/>
    /// </summary>
    public abstract class PresentProofV1_0 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public PresentProofV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        /// <param name="threadId">threadId given ID used for the thread. MUST not be null.</param>
        public PresentProofV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for the message family. Uses the community qualifier.
        /// </summary>
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;

        /// <summary>
        /// The name for the message family.
        /// </summary>
        string FAMILY = "present-proof";

        /// <summary>
        /// The version for the message family.
        /// </summary>
        string VERSION = "1.0";


        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

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
        /// <param name="value">the value the attribute must be greater than</param>
        /// <param name="restrictions">an array restrictions to be used</param>
        /// <returns>A Predicate with the given name, value and restrictions</returns>
        public static Predicate predicate(string name, int value, params Restriction[] restrictions)
        {
            return new Predicate(name, value, restrictions);
        }

        /// <summary>
        /// Directs verity-application to request a presentation of proof. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void request(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="request(Context)"/>
        public abstract JsonObject requestMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="request(Context)"/>
        public abstract byte[] requestMsgPacked(Context context);

        /// <summary>
        /// Directs verity-application to accept the request to present proof.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void accept(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public abstract JsonObject acceptMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public abstract byte[] acceptMsgPacked(Context context);

        /// <summary>
        /// Directs verity-application to reject this presentation proof protocol 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        public abstract void reject(Context context, string reason);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="reject(Context, string)"/>
        public abstract JsonObject rejectMsg(Context context, string reason);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="reason">an human readable reason for the rejection</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="reject(Context, string)"/>
        public abstract byte[] rejectMsgPacked(Context context, string reason);

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void status(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="status(Context)"/>
        public abstract JsonObject statusMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="status(Context)"/>
        public abstract byte[] statusMsgPacked(Context context);
    }
}