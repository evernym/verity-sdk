using System;
using System.Json;

namespace VeritySDK
{
    /*
     * NON_VISIBLE
     *
     * This is an implementation of OutOfBandImplV1_0 but is not viable to user of Verity SDK. Created using the
     * static PresentProof class
     */
    public class OutOfBandImplV1_0 : OutOfBandV1_0
    {
        static string CONNECTION_INVITATION = "reuse";

        private string inviteUrl;
        private string forRelationship;

        public OutOfBandImplV1_0(string forRelationship, string inviteUrl)
        {
            this.forRelationship = forRelationship;
            this.inviteUrl = String.IsNullOrWhiteSpace(inviteUrl) ? null : inviteUrl;
        }

        public override JsonObject handshakeReuseMsg(Context context)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());
            rtn.Add("inviteUrl", this.inviteUrl);

            addThread(rtn);

            if (!String.IsNullOrWhiteSpace(this.forRelationship)) rtn.Add("~for_relationship", this.forRelationship);

            return rtn;
        }

        public override void handshakeReuse(Context context)
        {
            send(context, handshakeReuseMsg(context));
        }

        public override byte[] handshakeReuseMsgPacked(Context context)
        {
            return packMsg(context, handshakeReuseMsg(context));
        }
    }
}