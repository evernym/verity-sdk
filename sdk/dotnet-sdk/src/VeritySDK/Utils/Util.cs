using Com.Evernym.Vdrtools;
using Com.Evernym.Vdrtools.CryptoApi;
using Com.Evernym.Vdrtools.WalletApi;
using System;
using System.IO;
using System.Json;
using System.Linq;
using System.Text;
using VeritySDK.Exceptions;
using VeritySDK.Protocols;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Static Utilities helper functions used for verity-sdk
    /// </summary>
    public class Util
    {
        public static bool USE_NEW_QUALIFIER_FORMAT = false;

        /// <summary>
        /// QUALIFIER for evernym specific protocols
        /// </summary>
        public static string EVERNYM_MSG_QUALIFIER = USE_NEW_QUALIFIER_FORMAT ? "https://didcomm.evernym.com" : "did:sov:123456789abcdefghi1234;spec";

        /// <summary>
        /// QUALIFIER for community specified protocol
        /// </summary>
        public static string COMMUNITY_MSG_QUALIFIER = USE_NEW_QUALIFIER_FORMAT ? "https://didcomm.org" : "did:sov:BzCbsNYhMrjHiqZDTUASHg;spec";

        /// <summary>
        /// Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
        /// public keys for encryption. The encryption and instructor is defined by the Aries community.
        /// </summary>
        /// <param name="walletHandle">the handle to a created and open Indy wallet</param>
        /// <param name="message">the JSON message to be communicated to the verity-application</param>
        /// <param name="domainDID">the domain DID of the intended recipient agent on the verity-application</param>
        /// <param name="remoteVerkey">the verkey for the agent on the verity-application</param>
        /// <param name="localVerkey">the authorized verkey in the local wallet for the verity-sdk application</param>
        /// <param name="publicVerkey">the public verkey for the verity-application</param>
        /// <return> the byte array of the packaged and encrypted message</return>
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
            catch (IndyException e)
            {
                throw new WalletException("Unable to pack messages", e);
            }
        }

        /// <summary>
        /// Packages message (instructor and encryption) for the verity-application. Uses local private keys and remote
        /// public keys for encryption. The encryption and instructor is defined by the Aries community.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="message">the JSON message to be communicated to the verity-application</param>
        /// <return>the byte array of the packaged and encrypted message</return>
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

        /// <summary>
        /// Prepares (pre-encryption) a forward message to a given DID that contains the given byte array message
        /// </summary>
        /// <param name="DID">the DID the forward message is intended for</param>
        /// <param name="message">the packaged and encrypted message that is being forwarded</param>
        /// <return>the prepared JSON forward structure</return>
        private static string prepareForwardMessage(string DID, byte[] message)
        {
            JsonObject fwdMessage = new JsonObject();
            fwdMessage.Add("@type", "did:sov:123456789abcdefghi1234;spec/routing/1.0/FWD");
            fwdMessage.Add("@fwd", DID);
            fwdMessage.Add("@msg", JsonObject.Parse(Encoding.UTF8.GetString(message)));

            return fwdMessage.ToString();
        }

        /// <summary>
        /// Extracts the message in the byte array that has been packaged and encrypted for a key that is locally held.
        /// </summary>
        /// <param name="context">an instance of the Context object initialized to a verity-application agent</param>
        /// <param name="message">the raw message received from the verity-application agent</param>
        /// <return>an unencrypted messages as a JSON object</return>
        public static JsonObject unpackMessage(Context context, byte[] message)
        {
            try
            {
                byte[] jwe = Crypto.UnpackMessageAsync(context.WalletHandle(), message).GetAwaiter().GetResult();

                var jwe_str = Encoding.UTF8.GetString(jwe);
                var jwe_json = JsonObject.Parse(jwe_str) as JsonObject;
                var msg = jwe_json.GetValue("message");
                var result = JsonObject.Parse(msg) as JsonObject;

                return result;
            }
            catch (IndyException e)
            {
                throw new WalletException("Unable to unpack message", e);
            }
        }


        /// <summary>
        /// Combines elements the given of a message family static values with the given message name to produce a fully
        /// qualified message type
        /// </summary>
        /// <param name="f">the given message family</param>
        /// <param name="msgName">the given message name</param>
        /// <return>a fully qualified message type</return>
        public static string getMessageType(MessageFamily f, string msgName)
        {
            return getMessageType(f.qualifier(), f.family(), f.version(), msgName);
        }

        /// <summary>
        /// Combines the given elements to produce a fully qualified message type
        /// </summary>
        /// <param name="msgQualifier">the given qualifier</param>
        /// <param name="msgFamily">the given family name</param>
        /// <param name="msgFamilyVersion">the given version</param>
        /// <param name="msgName">the given message name</param>
        /// <return>a fully qualified message type</return>
        public static string getMessageType(string msgQualifier, string msgFamily, string msgFamilyVersion, string msgName)
        {
            return msgQualifier + "/" + msgFamily + "/" + msgFamilyVersion + "/" + msgName;
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
