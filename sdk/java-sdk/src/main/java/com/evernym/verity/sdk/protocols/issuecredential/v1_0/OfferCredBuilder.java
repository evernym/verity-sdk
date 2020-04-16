package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;

public class OfferCredBuilder extends CredRestrictionBuilder<OfferCredBuilder> {

    public static OfferCredBuilder blank() {
        return new OfferCredBuilder();
    }

    @Override
    protected OfferCredBuilder self() {
        return this;
    }

    public OfferCred build() {
        return new OfferCred(getJSONObject());
    }
}
