package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;
import org.json.JSONObject;

import java.util.List;

public class OfferCredBuilder extends CredRestrictionBuilder<OfferCredBuilder> {

    public static OfferCredBuilder blank() {
        return new OfferCredBuilder();
    }

    public OfferCredBuilder credentialPreview(List<CredPreviewAttribute> attributes, MessageFamily msgFamily) {
        JSONObject jsonObject = CredPreviewBuilder
                .blank()
                .type(msgFamily.getMessageType("credential-preview"))
                .attributes(attributes)
                .build()
                .toJson();
        addToJSON("credential_preview", jsonObject);
        return this;
    }

    @Override
    protected OfferCredBuilder self() {
        return this;
    }

    public OfferCred build() {
        return new OfferCred(getJSONObject());
    }
}
