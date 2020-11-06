using System;
using System.Json;
using System.Security.Policy;

namespace VeritySDK
{
    /// <summary>
    /// This is an implementation of RelationshipImplV1_0 but is not viable to user of Verity SDK. Created using the static Relationship class
    /// </summary>
    public class RelationshipImplV1_0 : RelationshipV1_0
    {
        static string CREATE = "create";
        static string CONNECTION_INVITATION = "connection-invitation";
        static string SMS_CONNECTION_INVITATION = "sms-connection-invitation";
        static string OUT_OF_BAND_INVITATION = "out-of-band-invitation";
        static string SMS_OUT_OF_BAND_INVITATION = "sms-out-of-band-invitation";

        string forRelationship;
        string label;
        Url logoUrl = null;
        string phoneNumber = null;

        // flag if this instance started the interaction
        bool created = false;

        public RelationshipImplV1_0()
        {
            this.created = true;
        }

        public RelationshipImplV1_0(string label)
        {
            this.label = label;
            this.created = true;
        }

        public RelationshipImplV1_0(string label, Url logoUrl)
        {
            this.label = label;
            this.logoUrl = logoUrl;

            this.created = true;
        }

        public RelationshipImplV1_0(string label, Url logoUrl, string phoneNumber)
        {
            if (String.IsNullOrWhiteSpace(label))
            {
                this.label = label;
            }
            else
            {
                this.label = "";
            }
            this.logoUrl = logoUrl;
            this.phoneNumber = phoneNumber;

            this.created = true;
        }

        public RelationshipImplV1_0(string forRelationship, string threadId) : base(threadId)
        {
            this.forRelationship = forRelationship;
        }

        public override JsonObject createMsg(Context context)
        {
            if (!created)
            {
                throw new ArgumentException("Unable to create relationship when NOT starting the interaction");
            }

            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CREATE));
            rtn.Add("@id", getNewId());
            if (label != null)
                rtn.Add("label", label.ToString());
            if (logoUrl != null)
                rtn.Add("logoUrl", logoUrl.ToString());
            if (String.IsNullOrWhiteSpace(phoneNumber))
                rtn.Add("phoneNumber", phoneNumber);
            addThread(rtn);
            return rtn;
        }


        public override void create(Context context)
        {
            send(context, createMsg(context));
        }


        public override byte[] createMsgPacked(Context context)
        {
            return packMsg(context, createMsg(context));
        }


        public override void connectionInvitation(Context context)
        {
            send(context, connectionInvitationMsg(context));
        }


        public override void connectionInvitation(Context context, bool shortInvite)
        {
            send(context, connectionInvitationMsg(context, shortInvite));
        }


        public override JsonObject connectionInvitationMsg(Context context)
        {
            return connectionInvitationMsg(context, null);
        }


        public override JsonObject connectionInvitationMsg(Context context, bool? shortInvite)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());
            rtn.Add("shortInvite", shortInvite);

            if (String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);

            addThread(rtn);

            return rtn;
        }


        public override byte[] connectionInvitationMsgPacked(Context context)
        {
            return packMsg(context, connectionInvitationMsg(context));
        }


        public override byte[] connectionInvitationMsgPacked(Context context, bool shortInvite)
        {
            return packMsg(context, connectionInvitationMsg(context, shortInvite));
        }


        public override void smsConnectionInvitation(Context context)
        {
            send(context, smsConnectionInvitationMsg(context));
        }


        public override JsonObject smsConnectionInvitationMsg(Context context)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(SMS_CONNECTION_INVITATION));
            rtn.Add("@id", getNewId());

            if (String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);

            addThread(rtn);
            return rtn;
        }


        public override byte[] smsConnectionInvitationMsgPacked(Context context)
        {
            return packMsg(context, smsConnectionInvitationMsg(context));
        }


        public override void outOfBandInvitation(Context context)
        {
            send(context, outOfBandInvitationMsg(context));
        }


        public override void outOfBandInvitation(Context context, bool shortInvite)
        {
            send(context, outOfBandInvitationMsg(context, shortInvite));
        }


        public override void outOfBandInvitation(Context context, bool shortInvite, GoalCode goal)
        {
            send(context, outOfBandInvitationMsg(context, shortInvite, goal));
        }


        public override JsonObject outOfBandInvitationMsg(Context context)
        {
            return outOfBandInvitationMsg(context, null, null);
        }


        public override JsonObject outOfBandInvitationMsg(Context context, bool shortInvite)
        {
            return outOfBandInvitationMsg(context, shortInvite, null);
        }


        public override JsonObject outOfBandInvitationMsg(Context context, bool? shortInvite, GoalCode? goal)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(OUT_OF_BAND_INVITATION));
            rtn.Add("@id", getNewId());

            if (goal == null)
            {
                rtn.Add("goalCode", GoalCode.P2P_MESSAGING.code());
                rtn.Add("goal", GoalCode.P2P_MESSAGING.goalName());
            }
            else
            {
                rtn.Add("goalCode", goal?.code());
                rtn.Add("goal", goal?.goalName());
            }

            if (shortInvite != null)
            {
                rtn.Add("shortInvite", shortInvite);
            }

            if (String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);
            addThread(rtn);
            return rtn;
        }


        public override byte[] outOfBandInvitationMsgPacked(Context context)
        {
            return packMsg(context, outOfBandInvitationMsg(context));
        }



        public override byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite)
        {
            return packMsg(context, outOfBandInvitationMsg(context, shortInvite));
        }


        public override byte[] outOfBandInvitationMsgPacked(Context context, bool shortInvite, GoalCode goal)
        {
            return packMsg(context, outOfBandInvitationMsg(context, shortInvite, goal));
        }


        public override void smsOutOfBandInvitation(Context context)
        {
            send(context, smsOutOfBandInvitationMsg(context));
        }


        public override void smsOutOfBandInvitation(Context context, GoalCode goal)
        {
            send(context, smsOutOfBandInvitationMsg(context, goal));
        }


        public override JsonObject smsOutOfBandInvitationMsg(Context context)
        {
            return smsOutOfBandInvitationMsg(context, null);
        }


        public override JsonObject smsOutOfBandInvitationMsg(Context context, GoalCode? goal)
        {
            JsonObject rtn = new JsonObject();
            rtn.Add("@type", messageType(SMS_OUT_OF_BAND_INVITATION));
            rtn.Add("@id", getNewId());

            if (goal == null)
            {
                rtn.Add("goalCode", GoalCode.P2P_MESSAGING.code());
                rtn.Add("goal", GoalCode.P2P_MESSAGING.goalName());
            }
            else
            {
                rtn.Add("goalCode", goal?.code());
                rtn.Add("goal", goal?.goalName());
            }

            if (String.IsNullOrWhiteSpace(forRelationship))
                rtn.Add("~for_relationship", forRelationship);
            addThread(rtn);
            return rtn;
        }


        public override byte[] smsOutOfBandInvitationMsgPacked(Context context)
        {
            return packMsg(context, smsOutOfBandInvitationMsg(context));
        }


        public override byte[] smsOutOfBandInvitationMsgPacked(Context context, GoalCode goal)
        {
            return packMsg(context, smsOutOfBandInvitationMsg(context, goal));
        }
    }
}