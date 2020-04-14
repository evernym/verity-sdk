package com.evernym.verity.sdk.protocols.updateconfigs.v0_6;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;


public interface UpdateConfigsV0_6 extends MessageFamily {
    String QUALIFIER = Util.EVERNYM_MSG_QUALIFIER;
    String FAMILY = "configs";
    String VERSION = "0.6";

    default String qualifier() {return QUALIFIER;}
    default String family() { return FAMILY;}
    default String version() {return VERSION;}

    String UPDATE_CONFIGS = "UPDATE_CONFIGS";



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
}