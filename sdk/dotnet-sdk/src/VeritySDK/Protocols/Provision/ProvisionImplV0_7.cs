using Hyperledger.Indy;
using Hyperledger.Indy.CryptoApi;
using System;
using System.Json;
using System.Text;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of ProvisionImplV0_7 but is not viable to user of Verity SDK.Created using the static Provision class
    /// </summary>
    public class ProvisionImplV0_7 : ProvisionV0_7
    {

        private string token;

        public ProvisionImplV0_7() { }

        public ProvisionImplV0_7(string token)
        {
            validateToken(token);
            this.token = token;
        }

        public void validateToken(string token)
        {
            var tokenObj = JsonObject.Parse(token);
            byte[] data = Encoding.UTF8.GetBytes($"{tokenObj["nonce"]}{tokenObj["timestamp"]}{tokenObj["sponseeId"]}{tokenObj["sponsorId"]}");

            try
            {
                bool valid = Crypto.VerifyAsync(
                    tokenObj["sponsorVerKey"].ToString(),
                    data,
                    System.Convert.FromBase64String(tokenObj["sig"].ToString())
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
            catch (Exception /*InterruptedException | ExecutionException*/ e)
            {
                throw new ProvisionTokenException("Unable to validate signature -- signature does not validate", e);
            }
        }

        protected JsonObject sendToVerity(Context context, byte[] packedMessage)
        {
            Transport transport = new Transport();

            byte[] respBytes = transport.sendSyncMessage(context.VerityUrl(), packedMessage);
            return Util.unpackMessage(context, respBytes);
        }

        public override Context provision(Context context)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            JsonObject resp = sendToVerity(context, provisionMsgPacked(context));

            if (resp["@type"].Equals(messageType("problem-report")))
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

        public override JsonObject provisionMsg(Context context)
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
                //JObject tokenObj = JObject.Parse(token);

                //JsonObject tokenObj = new JsonObject(token);
                rtn.Add("provisionToken", token);
            }

            return rtn;
        }


        public override byte[] provisionMsgPacked(Context context)
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