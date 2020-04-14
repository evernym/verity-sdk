package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.common.BaseMsg;
import org.json.JSONObject;

public class CredPreview extends BaseMsg {

    public CredPreview(JSONObject data) {
        super(data);
    }
}