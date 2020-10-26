using System.Json;

namespace VeritySDK
{
    /**
     * An interface for controlling a 1.0 PresentProof protocol.
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/4fae574c03f9f1013db30bf2c0c676b1122f7149/features/0037-present-proof" target="_blank" rel="noopener noreferrer">Aries 0037: Present Proof Protocol 1.0</a>
     */

    public abstract class PresentProofV1_0 : AbstractProtocol
    {
        public PresentProofV1_0() { }
        public PresentProofV1_0(string threadId) : base(threadId) { }

        /**
         * The qualifier for the message family. Uses the community qualifier.
         */
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        /**
         * The name for the message family.
         */
        string FAMILY = "present-proof";
        /**
         * The version for the message family.
         */
        string VERSION = "1.0";


    /**
     * @see MessageFamily#qualifier()
     */
    public override string qualifier() { return QUALIFIER; }
    /**
     * @see MessageFamily#family()
     */
    public override string family() { return FAMILY; }
    /**
     * @see MessageFamily#version()
     */
    public override string version() { return VERSION; }

        /**
         * Creates an attribute restriction for a presentation of proof
         * @param name the attribute name to apply the restriction
         * @param restrictions an array restrictions to be used
         * @return An Attribute with the given name and restrictions
         */
        public static Attribute attribute(string name, params Restriction[] restrictions)
        {
            return new Attribute(name, restrictions);
        }

        /**
         * Creates a predicate restriction for a presentation of proof. Indy Anoncreds only supports, so the
         * value should be expressed as a greater than predicate.
         *
         * @param name the attribute name to apply the restriction
         * @param value the value the attribute must be greater than
         * @param restrictions an array restrictions to be used
         * @return A Predicate with the given name, value and restrictions
         */
        public static Predicate predicate(string name, int value, params Restriction[] restrictions)
        {
            return new Predicate(name, value, restrictions);
        }


        /**
         * Directs verity-application to request a presentation of proof.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void request(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #request
         */
        public abstract JsonObject requestMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #request
         */
        public abstract byte[] requestMsgPacked(Context context);

        /**
         * Directs verity-application to accept the request to present proof.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void accept(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #accept
         */
        public abstract JsonObject acceptMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #accept
         */
        public abstract byte[] acceptMsgPacked(Context context);


        /**
         * Directs verity-application to reject this presentation proof protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param reason an human readable reason for the rejection
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void reject(Context context, string reason);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #reject
         */
        public abstract JsonObject rejectMsg(Context context, string reason);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #reject
         */
        public abstract byte[] rejectMsgPacked(Context context, string reason);

        /**
         * Ask for status from the verity-application agent
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @throws VerityException when wallet operations fails or given invalid context
         */
        public abstract void status(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @throws VerityException when given invalid context
         *
         * @see #status
         */
        public abstract JsonObject statusMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @throws VerityException when wallet operations fails or given invalid context
         *
         * @see #status
         */
        public abstract byte[] statusMsgPacked(Context context);
    }
}