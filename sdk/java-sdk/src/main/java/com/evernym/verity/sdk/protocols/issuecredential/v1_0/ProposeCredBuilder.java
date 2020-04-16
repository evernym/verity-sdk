package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;

public class ProposeCredBuilder extends CredRestrictionBuilder<ProposeCredBuilder> {

    public static ProposeCredBuilder blank() {
        return new ProposeCredBuilder();
    }

    public ProposeCredBuilder comment(String comment) {
        addToJSON("comment", comment);
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
