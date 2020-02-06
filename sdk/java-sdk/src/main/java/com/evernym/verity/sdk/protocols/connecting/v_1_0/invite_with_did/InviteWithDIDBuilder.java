package com.evernym.verity.sdk.protocols.connecting.v_1_0.invite_with_did;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;

public class InviteWithDIDBuilder extends BaseMsgBuilder<InviteWithDIDBuilder> implements AsJsonObject  {

    public static InviteWithDIDBuilder blank() {
        return new InviteWithDIDBuilder();
    }

    public InviteWithDIDBuilder did(String did) {
        addToJSON("did", did);
        return this;
    }

    public InviteWithDIDBuilder label(String label) {
        addToJSON("label", label);
        return this;
    }

    public InviteWithDID build() {
        return new InviteWithDID(getJSONObject());
    }

    @Override
    protected InviteWithDIDBuilder self() {
        return this;
    }
}
