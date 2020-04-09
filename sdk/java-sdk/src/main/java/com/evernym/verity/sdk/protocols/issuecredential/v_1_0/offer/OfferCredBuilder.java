package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.offer;

import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewAttribute;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewBuilder;
import org.json.JSONObject;

import java.util.List;

public class OfferCredBuilder extends CredRestrictionBuilder<OfferCredBuilder> {

    public static OfferCredBuilder blank() {
        return new OfferCredBuilder();
    }

    public OfferCredBuilder comment(String comment) {
        addToJSON("comment", comment);
        return this;
    }

    public OfferCredBuilder credentialPrevious(List<CredPreviewAttribute> attributes, MessageFamily msgFamily) {
        JSONObject jsonObject = CredPreviewBuilder
                .blank()
                .type(msgFamily.getMessageType("credential-preview"))
                .attributes(attributes)
                .build()
                .toJson();
        addToJSON("credentialPreview", jsonObject);
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
