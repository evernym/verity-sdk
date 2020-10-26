using Hyperledger.Indy;
using Hyperledger.Indy.CryptoApi;
using Hyperledger.Indy.WalletApi;
using System;
using System.IO;
using System.Json;
using System.Linq;
using System.Text;

namespace VeritySDK
{
    /**
     * Static Utilities helper functions used for verity-sdk
     */
    public class Util
    {
        /**
         * QUALIFIER for evernym specific protocols
         */
        public static string EVERNYM_MSG_QUALIFIER = "did:sov:123456789abcdefghi1234";

        /**
         * QUALIFIER for community specified protocol
         */
        public static string COMMUNITY_MSG_QUALIFIER = "did:sov:BzCbsNYhMrjHiqZDTUASHg";

        /**
         * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
         * public keys for encryption. The encryption and instructor is defined by the Aries community.
         *
         * @param walletHandle the handle to a created and open Indy wallet
         * @param message the JSON message to be communicated to the verity-application
         * @param domainDID the domain DID of the intended recipient agent on the verity-application
         * @param remoteVerkey the verkey for the agent on the verity-application
         * @param localVerkey the authorized verkey in the local wallet for the verity-sdk application
         * @param publicVerkey the public verkey for the verity-application
         * @return the byte array of the packaged and encrypted message
         * @throws WalletException when wallet operations fails
         *
         * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
         */
        public static byte[] packMessageForVerity(Wallet walletHandle,
                                                  JsonObject message,
                                                  string domainDID,
                                                  string remoteVerkey,
                                                  string localVerkey,
                                                  string publicVerkey)
        {            
            try
            {
                string pairwiseReceiver = new JsonArray(remoteVerkey).ToString();
                string verityReceiver = new JsonArray(publicVerkey).ToString();

                byte[] agentMessage = Crypto.PackMessageAsync(
                        walletHandle,
                        pairwiseReceiver,
                        localVerkey,
                        Encoding.UTF8.GetBytes(message.ToString())
                ).GetAwaiter().GetResult();

                string innerFwd = prepareForwardMessage(domainDID, agentMessage);

                var result = Crypto.PackMessageAsync(
                        walletHandle,
                        verityReceiver,
                        null,
                        Encoding.UTF8.GetBytes(innerFwd)
                ).GetAwaiter().GetResult();

                return result;
            }
            catch (IndyException /*| InterruptedException | ExecutionException*/ e)
            {
                throw new WalletException("Unable to pack messages", e);
            }
        }

        /**
         * Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
         * public keys for encryption. The encryption and instructor is defined by the Aries community.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param message the JSON message to be communicated to the verity-application
         * @return the byte array of the packaged and encrypted message
         * @throws WalletException when wallet operations fails (including encryption)
         * @throws UndefinedContextException when the context is missing required fields (domainDID, verityAgentVerKey, sdkVerKey, verityPublicVerKey)
         *
         * @see <a href="https://github.com/hyperledger/aries-rfcs/tree/9b0aaa39df7e8bd434126c4b33c097aae78d65bf/features/0019-encryption-envelope" target="_blank" rel="noopener noreferrer">Aries 0019: Encryption Envelope</a>
         */
        public static byte[] packMessageForVerity(Context context, JsonObject message)
        {
            if (context == null)
            {
                throw new UndefinedContextException("Context cannot be NULL");
            }

            Wallet handle = context.WalletHandle();
            return packMessageForVerity(
                    handle,
                    message,
                    context.DomainDID(),
                    context.VerityAgentVerKey(),
                    context.SdkVerKey(),
                    context.VerityPublicVerKey()
            );
        }

        /**
         * Prepares (pre-encryption) a forward message to a given DID that contains the given byte array message
         * @param DID the DID the forward message is intended for
         * @param message the packaged and encrypted message that is being forwarded
         * @return the prepared JSON forward structure
         */
        private static string prepareForwardMessage(string DID, byte[] message)
        {
            //JObject mess = new JObject();
            //mess.Add("@type", "did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD");
            //mess.Add("@fwd", DID);
            //mess.Add("@msg", JObject.Parse(Encoding.UTF8.GetString(message)));

            //return mess.ToString();

            JsonObject fwdMessage = new JsonObject();
            fwdMessage.Add("@type", "did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD");
            fwdMessage.Add("@fwd", DID);
            fwdMessage.Add("@msg", JsonObject.Parse(Encoding.UTF8.GetString(message)));

            return fwdMessage.ToString();
        }

        /**
         * Extracts the message in the byte array that has been packaged and encrypted for a key that is locally held.
         *
         * @param context an instance of the Context object initialized to a verity-application agent
         * @param message the raw message received from the verity-application agent
         * @return an unencrypted messages as a JSON object
         * @throws WalletException when wallet operations fails (including decryption)
         */
        public static JsonObject unpackMessage(Context context, byte[] message)
        {
            try
            {
                byte[] jwe = Crypto.UnpackMessageAsync(context.WalletHandle(), message).GetAwaiter().GetResult();

                var jwe_str = Encoding.UTF8.GetString(jwe);
                var jwe_json = JsonObject.Parse(jwe_str) as JsonObject;
                var msg = jwe_json.Get("message");
                var result = JsonObject.Parse(msg) as JsonObject;

                return result;
            }
            catch (IndyException /*| InterruptedException | ExecutionException*/ e)
            {
                throw new WalletException("Unable to unpack message", e);
            }
        }


        /**
         * Combines elements the given of a message family static values with the given message name to produce a fully
         * qualified message type
         * @param f the given message family
         * @param msgName the given message name
         * @return a fully qualified message type
         */
        public static string getMessageType(MessageFamily f, string msgName)
        {
            return getMessageType(f.qualifier(), f.family(), f.version(), msgName);
        }

        /**
         * Combines the given elements to produce a fully qualified message type
         * @param msgQualifier the given qualifier
         * @param msgFamily the given family name
         * @param msgFamilyVersion the given version
         * @param msgName the given message name
         * @return a fully qualified message type
         */
        public static string getMessageType(string msgQualifier, string msgFamily, string msgFamilyVersion, string msgName)
        {
            return msgQualifier + ";spec/" + msgFamily + "/" + msgFamilyVersion + "/" + msgName;
        }

        [Obsolete]
        public static string getProblemReportMessageType(string msgQualifier, string msgFamily, string msgFamilyVersion)
        {
            return Util.getMessageType(msgQualifier, msgFamily, msgFamilyVersion, "problem-report");
        }

        [Obsolete]
        public static string getStatusMessageType(string msgQualifier, string msgFamily, string msgFamilyVersion)
        {
            return Util.getMessageType(msgQualifier, msgFamily, msgFamilyVersion, "status");
        }
    }
}
