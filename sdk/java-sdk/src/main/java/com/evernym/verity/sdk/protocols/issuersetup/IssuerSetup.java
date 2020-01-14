package com.evernym.verity.sdk.protocols.issuersetup;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
public interface IssuerSetup extends MessageFamily {
    String MSG_QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String MSG_FAMILY = "issuer-setup";
    String MSG_FAMILY_VERSION = "0.6";

    default String qualifier() {return MSG_QUALIFIER;}
    default String family() {return MSG_FAMILY;}
    default String version() {return MSG_FAMILY_VERSION;}

    String CREATE = "create";
    String CURRENT_PUBLIC_IDENTIFIER = "current-public-identifier";

    static IssuerSetup v0_6() {
        return new IssuerSetupImpl();
    }

    void create(Context context) throws IOException, VerityException;
    JSONObject createMsg(Context context);
    byte[]  createMsgPacked(Context context) throws VerityException;

    void currentPublicIdentifier(Context context) throws IOException, VerityException;
    JSONObject currentPublicIdentifierMsg(Context context);
    byte[]  currentPublicIdentifierMsgPacked(Context context) throws VerityException;
}
