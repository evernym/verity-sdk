package com.evernym.verity.sdk.protocols.issuecredential.v1_0;

import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;

public class CredReqBuilder extends CredRestrictionBuilder<CredReqBuilder> {

    public static CredReqBuilder blank() {
        return new CredReqBuilder();
    }

    @Override
    protected CredReqBuilder self() {
        return this;
    }

    public CredReq build() {
        return new CredReq(getJSONObject());
    }
}
