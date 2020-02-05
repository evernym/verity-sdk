package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class CredPreviewAttribute implements AsJsonObject {
    JSONObject data;

    public CredPreviewAttribute(String name, String mimeType, String value) {
        this.data = new JSONObject()
                .put("name", name)
                .put("value", value);
        if (mimeType != null){
            this.data.put("mime-type", mimeType);
        }
    }

    @Override
    public JSONObject toJson() { return data; }
}
