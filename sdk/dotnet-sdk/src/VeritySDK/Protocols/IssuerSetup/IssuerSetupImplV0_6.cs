using System.Json;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     *
     * This is an implementation of IssuerSetupImplV0_6 but is not viable to user of Verity SDK. Created using the
     * static IssuerSetup class
     */
    public class IssuerSetupImplV0_6 : IssuerSetupV0_6
    {
        public IssuerSetupImplV0_6() { }

        public override void create(Context context)
        {
            send(context, createMsg(context));
        }

        public override JsonObject createMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CREATE));
            message.Add("@id", getNewId());
            addThread(message);
            return message;
        }

        public override byte[] createMsgPacked(Context context)
        {
            return packMsg(context, createMsg(context));
        }

        public override void currentPublicIdentifier(Context context)
        {
            send(context, currentPublicIdentifierMsg(context));
        }

        public override JsonObject currentPublicIdentifierMsg(Context context)
        {
            JsonObject message = new JsonObject();
            message.Add("@type", messageType(CURRENT_PUBLIC_IDENTIFIER));
            message.Add("@id", getNewId());
            addThread(message);
            return message;
        }

        public override byte[] currentPublicIdentifierMsgPacked(Context context)
        {
            return packMsg(context, currentPublicIdentifierMsg(context));
        }
    }
}