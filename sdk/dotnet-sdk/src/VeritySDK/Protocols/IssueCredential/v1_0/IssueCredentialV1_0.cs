using System;
using System.Collections.Generic;
using System.Json;
using System.Text;

namespace VeritySDK
{
    /**
     * An interface for controlling a 1.0 IssueCredential protocol.
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/bb42a6c35e0d5543718fb36dd099551ab192f7b0/features/0036-issue-credential" target="_blank" rel="noopener noreferrer">Aries 0036: Issue Credential Protocol 1.0</a>
     */
    public abstract class IssueCredentialV1_0 : AbstractProtocol
    {
        public IssueCredentialV1_0() { }
        public IssueCredentialV1_0(string threadId) : base(threadId) { }
        /**
         * The qualifier for message family. Uses the community qualifier.
         */
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
        /**
         * The name for message family.
         */
        string FAMILY = "issue-credential";
        /**
         * The version for message family.
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
         * Directs verity-application to send credential proposal.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void proposeCredential(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #proposeCredential
         */
        public abstract JsonObject proposeCredentialMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #proposeCredential
         */
        public abstract byte[] proposeCredentialMsgPacked(Context context);

        /**
         * Directs verity-application to send credential offer.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void offerCredential(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #offerCredential
         */
        public abstract JsonObject offerCredentialMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #offerCredential
         */
        public abstract byte[] offerCredentialMsgPacked(Context context);

        /**
         * Directs verity-application to send credential request.
         *
         * @param context
         * @throws IOException
         * @
         */
        public abstract void requestCredential(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #requestCredential
         */
        public abstract JsonObject requestCredentialMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #requestCredential
         */
        public abstract byte[] requestCredentialMsgPacked(Context context);

        /**
         * Directs verity-application to issue credential and send it
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void issueCredential(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #issueCredential
         */
        public abstract JsonObject issueCredentialMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #issueCredential
         */
        public abstract byte[] issueCredentialMsgPacked(Context context);

        /**
         * Directs verity-application to reject the credential protocol
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void reject(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #reject
         */
        public abstract JsonObject rejectMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #reject
         */
        public abstract byte[] rejectMsgPacked(Context context);

        /**
         * Ask for status from the verity-application agent
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @throws IOException when the HTTP library fails to post to the agency endpoint
         * @ when wallet operations fails or given invalid context
         */
        public abstract void status(Context context);

        /**
         * Creates the control message without packaging and sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the constructed message (JSON object)
         * @ when given invalid context
         *
         * @see #status
         */
        public abstract JsonObject statusMsg(Context context);

        /**
         * Creates and packages message without sending it.
         * @param context an instance of the Context object initialized to a verity-application agent
         * @return the byte array ready for transport
         * @ when wallet operations fails or given invalid context
         *
         * @see #status
         */
        public abstract byte[] statusMsgPacked(Context context);
    }
}
