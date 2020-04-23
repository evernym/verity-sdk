package com.evernym.verity.sdk.protocols.presentproof.common;

import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;

public class RestrictionBuilder extends CredRestrictionBuilder<RestrictionBuilder> {

    public static RestrictionBuilder blank() {
        return new RestrictionBuilder();
    }

    private RestrictionBuilder() {

    }

    public Restriction build() {
        return new Restriction(getJSONObject());
    }

    @Override
    protected RestrictionBuilder self() {
        return this;
    }
}
