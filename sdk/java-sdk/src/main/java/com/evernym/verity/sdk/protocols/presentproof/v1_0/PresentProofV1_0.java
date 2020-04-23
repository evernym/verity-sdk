package com.evernym.verity.sdk.protocols.presentproof.v1_0;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.presentproof.common.Attribute;
import com.evernym.verity.sdk.protocols.presentproof.common.Predicate;
import com.evernym.verity.sdk.protocols.presentproof.common.Restriction;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a message to Verity asking it to send a Proof Request to a connection
 */
public interface PresentProofV1_0 extends MessageFamily {

    String QUALIFIER = Util.COMMUNITY_MSG_QUALIFIER;
    String FAMILY = "present-proof";
    String VERSION = "1.0";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    static Attribute attribute(String name, Restriction... restrictions) {
        return new Attribute(name, restrictions);
    }

    static Predicate predicate(String name, int value, Restriction... restrictions) {
        return new Predicate(name, value, restrictions);
    }


    /**
     * Sends the proof request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void request(Context context) throws IOException, VerityException;

    JSONObject requestMsg(Context context) throws VerityException;

    byte[] requestMsgPacked(Context context) throws VerityException;


    void accept(Context context) throws IOException, VerityException;

    JSONObject acceptMsg(Context context) throws VerityException;

    byte[] acceptMsgPacked(Context context) throws VerityException;

    /**
     * Sends the status request message to Verity
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    JSONObject statusMsg(Context context) throws VerityException;

    byte[] statusMsgPacked(Context context) throws VerityException;
}