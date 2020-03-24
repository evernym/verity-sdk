package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONArray;

import java.util.List;

public class CredPreviewBuilder extends BaseMsgBuilder<CredPreviewBuilder> implements AsJsonObject  {

    public static CredPreviewBuilder blank() {
        return new CredPreviewBuilder();
    }

    public CredPreviewBuilder attributes(List<CredPreviewAttribute> attributes) {
        JSONArray ja = new JSONArray();
        if(attributes != null) {
            for(CredPreviewAttribute i: attributes) {
                if(i != null) {
                    ja.put(i.toJson());
                }
            }
        }

        addToJSON("attributes", ja);
        return this;
    }

    public CredPreview build() {
        return new CredPreview(getJSONObject());
    }

    @Override
    protected CredPreviewBuilder self() {
        return this;
    }
}
