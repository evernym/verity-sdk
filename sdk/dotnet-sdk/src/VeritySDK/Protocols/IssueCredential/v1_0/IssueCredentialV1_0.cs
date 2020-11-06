using System;
using System.Collections.Generic;
using System.Json;
using System.Text;

namespace VeritySDK
{

    /// <summary>
    /// An class  for controlling a 1.0 IssueCredential protocol. 
    /// </summary>
    /// <see cref="https://github.com/hyperledger/aries-rfcs/tree/bb42a6c35e0d5543718fb36dd099551ab192f7b0/features/0036-issue-credential"/>
    public abstract class IssueCredentialV1_0 : AbstractProtocol
    {
        /// <summary>
        /// Constructor
        /// </summary>
        public IssueCredentialV1_0() { }

        /// <summary>
        /// Constructor
        /// </summary>
        public IssueCredentialV1_0(string threadId) : base(threadId) { }

        /// <summary>
        /// The qualifier for message family. Uses the community qualifier.
        /// </summary>
        string QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;

        /// <summary>
        /// The name for message family.
        /// </summary>
        string FAMILY = "issue-credential";

        /// <summary>
        /// The version for message family.
        /// </summary>
        string VERSION = "1.0";

        /// <see cref="MessageFamily.qualifier"/>
        public override string qualifier() { return QUALIFIER; }

        /// <see cref="MessageFamily.family"/>
        public override string family() { return FAMILY; }

        /// <see cref="MessageFamily.version"/>
        public override string version() { return VERSION; }

        /// <summary>
        /// Directs verity-application to send credential proposal. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void proposeCredential(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="proposeCredential(Context)"/>
        public abstract JsonObject proposeCredentialMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="proposeCredential(Context)"/>
        public abstract byte[] proposeCredentialMsgPacked(Context context);

        /// <summary>
        /// Directs verity-application to send credential offer.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void offerCredential(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message(JSON object)</returns>
        /// <see cref="offerCredential(Context)"/>
        public abstract JsonObject offerCredentialMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="offerCredential(Context)"/>
        public abstract byte[] offerCredentialMsgPacked(Context context);


        /// <summary>
        /// Directs verity-application to send credential request. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void requestCredential(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="requestCredential(Context)"/>
        public abstract JsonObject requestCredentialMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="requestCredential(Context)"/>
        public abstract byte[] requestCredentialMsgPacked(Context context);


        /// <summary>
        /// Directs verity-application to issue credential and send it 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void issueCredential(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="issueCredential(Context)"/>
        public abstract JsonObject issueCredentialMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="issueCredential(Context)"/>
        public abstract byte[] issueCredentialMsgPacked(Context context);

        /// <summary>
        /// Directs verity-application to reject the credential protocol
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
        public abstract void reject(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        /// <see cref="reject(Context)"/>
        public abstract JsonObject rejectMsg(Context context);

        /// <summary>
        /// Creates and packages message without sending it.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        /// <see cref="reject(Context)"/>
        public abstract byte[] rejectMsgPacked(Context context);

        /// <summary>
        /// Ask for status from the verity-application agent 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        public abstract void status(Context context);

        /// <summary>
        /// Creates the control message without packaging and sending it.
        /// </summary>
        /// <param name="context">context an instance of the Context object initialized to a verity-application agent</param>
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
