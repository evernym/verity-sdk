package com.evernym.verity.sdk.protocols.relationship.v1_0;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface RelationshipV1_0 extends MessageFamily {
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String FAMILY = "relationship";
    String VERSION = "1.0";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    void create(Context context) throws IOException, VerityException;
    JSONObject createMsg(Context context) throws IOException, VerityException;
    byte[] createMsgPacked(Context context) throws IOException, VerityException;

    void connectionInvitation(Context context) throws IOException, VerityException;
    JSONObject connectionInvitationMsg(Context context) throws IOException, VerityException;
    byte[] connectionInvitationMsgPacked(Context context) throws IOException, VerityException;
}
