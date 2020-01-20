package com.evernym.verity.sdk.protocols.issuersetup;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;
public interface IssuerSetup extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "issuer-setup";}
    default String version() {return "0.6";}

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
