package com.evernym.verity.sdk.protocols.outofband.v1_0;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

public interface OutOfBandV1_0 extends MessageFamily {
    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    String FAMILY = "out-of-band";
    String VERSION = "1.0";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    void create(Context context) throws IOException, VerityException;
    JSONObject createMsg(Context context) throws IOException, VerityException;
    byte[] createMsgPacked(Context context) throws IOException, VerityException;

    void outOfBandInvitation(Context context) throws IOException, VerityException;
    JSONObject outOfBandInvitationMsg(Context context) throws IOException, VerityException;
    byte[] outOfBandInvitationMsgPacked(Context context) throws IOException, VerityException;


}
