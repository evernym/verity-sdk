package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;
import org.json.JSONObject;

import java.util.List;

public class ProposeCredBuilder extends CredRestrictionBuilder<ProposeCredBuilder> {

    public static ProposeCredBuilder blank() {
        return new ProposeCredBuilder();
    }

    public ProposeCredBuilder comment(String comment) {
        addToJSON("comment", comment);
        return this;
    }

    public ProposeCredBuilder proposal(List<CredPreviewAttribute> attributes, MessageFamily msgFamily) {
        JSONObject jsonObject = CredPreviewBuilder
                .blank()
                .type(msgFamily.getMessageType("credential-preview"))
                .attributes(attributes)
                .build()
                .toJson();
        addToJSON("credential_proposal", jsonObject);
        return this;
    }

    @Override
    protected ProposeCredBuilder self() {
        return this;
    }

    public ProposeCred build() {
        return new ProposeCred(getJSONObject());
    }
}
