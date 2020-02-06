package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONArray;

import java.util.List;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

public class CredPreviewBuilder extends BaseMsgBuilder<CredPreviewBuilder> implements AsJsonObject  {

    public static CredPreviewBuilder blank() {
        return new CredPreviewBuilder();
    }

    public CredPreviewBuilder attributes(List<CredPreviewAttribute> attributes) {
        JSONArray ja = makeArray(attributes);
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
