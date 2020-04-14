package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

public class CredPreviewAttribute implements AsJsonObject {
    JSONObject data;

    public CredPreviewAttribute(String name, String value, String mimeType) {
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
