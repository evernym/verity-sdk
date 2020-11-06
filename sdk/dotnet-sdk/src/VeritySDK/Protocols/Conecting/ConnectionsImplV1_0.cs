using System.Json;

namespace VeritySDK
{
    /// <summary>
    /// NON_VISIBLE
    /// This is an implementation of ConnectionsV1_0 but is not viable to user of Verity SDK.
    /// Created using the static Connecting class
    /// </summary>
    public class ConnectionsImplV1_0 : ConnectionsV1_0
    {
        string forRelationship;
        string label;
        string base64InviteURL;

        string ACCEPT_INVITE = "accept";
        string STATUS = "status";

        public ConnectionsImplV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
            this.label = null;
            this.base64InviteURL = null;
        }

        public ConnectionsImplV1_0(string forRelationship, string label, string base64InviteURL)
        {
            this.forRelationship = forRelationship;
            this.label = label;
            this.base64InviteURL = base64InviteURL;
        }

        public override void status(Context context)
        {
            send(context, statusMsg(context));
        }

        public override JsonObject statusMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(STATUS));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            return msg;
        }

        public override byte[] statusMsgPacked(Context context)
        {
            return packMsg(context, statusMsg(context));
        }

        public override JsonObject acceptMsg(Context context)
        {
            JsonObject msg = new JsonObject();
            msg.Add("@type", messageType(ACCEPT_INVITE));
            msg.Add("@id", getNewId());
            msg.Add("~for_relationship", forRelationship);
            addThread(msg);

            msg.Add("label", label);
            msg.Add("invite_url", base64InviteURL);

            return msg;
        }

        public override void accept(Context context)
        {
            send(context, acceptMsg(context));
        }

        public override byte[] acceptMsgPacked(Context context)
        {
            return packMsg(context, acceptMsg(context));
        }
    }
}
