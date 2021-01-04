using Hyperledger.Indy;
using Hyperledger.Indy.CryptoApi;
using System;
using System.Json;
using System.Text;
using VeritySDK.Exceptions;
using VeritySDK.Protocols;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Protocols.Provision
{
    /// <summary>
    /// A class for controlling a 0.7 Provision protocol.
    /// </summary>
    public class ProvisionV0_7 : AbstractProtocol
    {
        #region Protocol identificator

        /// <summary>
        /// The qualifier for the message family. Uses Evernym's qualifier.
        /// </summary>
        public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }

        /// <summary>
        /// The name for the message family.
        /// </summary>
        public override string family() { return "agent-provisioning"; }

        /// <summary>
        /// The version for the message family.
        /// </summary>
        public override string version() { return "0.7"; }

        #endregion 

        #region Constructors

        public ProvisionV0_7() { }

        public ProvisionV0_7(string token)
        {
            validateToken(token);
            this.token = token;
        }

        #endregion 

        string token;

        public void validateToken(string token)
        {
            var tokenObj = JsonObject.Parse(token) as JsonObject;
            byte[] data = Encoding.UTF8.GetBytes($"{tokenObj.getAsString("nonce")}{tokenObj.getAsString("timestamp")}{tokenObj.getAsString("sponseeId")}{tokenObj.getAsString("sponsorId")}");

            try
            {
                bool valid = Crypto.VerifyAsync(
                    tokenObj.getAsString("sponsorVerKey"),
                    data,
                    System.Convert.FromBase64String(tokenObj.getAsString("sig"))
                ).GetAwaiter().GetResult();

                //noinspection PointlessboolExpression
                if (valid == false)
                {
                    throw new ProvisionTokenException("Invalid provision token -- signature does not validate");
                }
            }
            catch (IndyException e)
            {
                throw new ProvisionTokenException("Invalid provision token -- signature does not validate", e);
            }
            catch (Exception e)
            {
                throw new ProvisionTokenException("Unable to validate signature -- signature does not validate", e);
            }
        }

        protected virtual JsonObject sendToVerity(Context context, byte[] packedMessage)
        {
            Transport transport = new Transport();

            byte[] respBytes = transport.sendSyncMessage(context.VerityUrl(), packedMessage);
            return Util.unpackMessage(context, respBytes);
        }


        /// <summary>
        /// Name for 'create-edge-agent' control message
        /// </summary>
        string CREATE_EDGE_AGENT = "create-edge-agent";


        /// <summary>
        /// Sends provisioning message that directs the creation of an agent to the to verity-application 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>new Context with provisioned details</returns>
        public Context provision(Context context)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            JsonObject resp = sendToVerity(context, provisionMsgPacked(context));

            if ( ((string) resp.GetValue("@type") ?? "").Equals(messageType("problem-report")) )
            {
                throw new ProvisionTokenException(resp["msg"]);
            }

            string domainDID = resp["selfDID"];
            string verityAgentVerKey = resp["agentVerKey"];

            return context.ToContextBuilder()
                    .domainDID(domainDID)
                    .verityAgentVerKey(verityAgentVerKey)
                    .build();
        }

        /// <summary>
        /// Creates the control message without packaging and sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the constructed message (JSON object)</returns>
        public JsonObject provisionMsg(Context context)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            JsonObject rtn = new JsonObject();
            rtn.Add("@id", AbstractProtocol.getNewId());
            rtn.Add("@type", messageType(CREATE_EDGE_AGENT));
            rtn.Add("requesterVk", context.SdkVerKey());

            if (token != null)
            {
                var tokenObj = JsonObject.Parse(token) as JsonObject;
                rtn.Add("provisionToken", tokenObj);
            }

            return rtn;
        }

        /// <summary>
        /// Creates and packages message without sending it. 
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <returns>the byte array ready for transport</returns>
        public byte[] provisionMsgPacked(Context context)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            return Util.packMessageForVerity(
                context.WalletHandle(),
                provisionMsg(context),
                context.VerityPublicDID(),
                context.VerityPublicVerKey(),
                context.SdkVerKey(),
                context.VerityPublicVerKey()
            );
        }

    }
}