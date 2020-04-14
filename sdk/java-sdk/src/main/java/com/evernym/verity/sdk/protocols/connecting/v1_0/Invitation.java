package com.evernym.verity.sdk.protocols.connecting.v1_0;

import com.evernym.verity.sdk.protocols.common.BaseMsg;
import org.json.JSONObject;

public class Invitation extends BaseMsg {

    public Invitation(JSONObject data) {
        super(data);
    }
}
