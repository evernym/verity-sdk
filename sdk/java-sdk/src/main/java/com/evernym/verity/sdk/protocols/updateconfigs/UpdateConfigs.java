package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;


public interface UpdateConfigs extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "update-configs";}
    default String version() {return "0.6";}

    String UPDATE_CONFIGS = "update";
    String GET_STATUS = "get-status";

    /**
     * Parameterized constructor
     * @param name Organization name
     * @param logoUrl Organization logo url
     */
    static UpdateConfigs v0_6(String name, String logoUrl) { return new UpdateConfigsImpl(name, logoUrl); }

    /**
     * Construtor useful only for status
     */
    static UpdateConfigs v0_6() { return new UpdateConfigsImpl(); }

    /**
     *
     * @param context
     * @throws IOException
     * @throws VerityException
     */
    void update(Context context) throws IOException, VerityException;

    /**
     *
     * @param context
     * @return
     * @throws UndefinedContextException
     */
    JSONObject updateMsg(Context context) throws UndefinedContextException;

    /**
     *
     * @param context
     * @return
     * @throws VerityException
     */
    byte[] updateMsgPacked(Context context) throws VerityException;


    /**
     * Sends the get status message to the connection
     * @param context an instance of Context configured with the results of the provision_sdk.py script
     * @throws IOException               when the HTTP library fails to post to the agency endpoint
     * @throws UndefinedContextException when the context doesn't have enough information for this operation
     * @throws WalletException when there are issues with encryption and decryption
     */
    void status(Context context) throws IOException, VerityException;

    JSONObject statusMsg(Context context) throws VerityException;

    byte[] statusMsgPacked(Context context) throws VerityException;
}