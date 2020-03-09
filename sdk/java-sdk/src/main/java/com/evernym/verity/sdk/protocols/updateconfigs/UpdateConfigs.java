package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONObject;

import java.io.IOException;


public interface UpdateConfigs extends MessageFamily {
    default String qualifier() {return Util.EVERNYM_MSG_QUALIFIER;}
    default String family() {return "configs";}
    default String version() {return "0.6";}

    String UPDATE_CONFIGS = "UPDATE_CONFIGS";

    static UpdateConfigs v0_6(String name, String logoUrl) { return new UpdateConfigsImpl(name, logoUrl); }

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