package com.evernym.verity.sdk.protocols.connecting.v_1_0.invited;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;

public class InvitedBuilder extends BaseMsgBuilder<InvitedBuilder> implements AsJsonObject  {

    public static InvitedBuilder blank() {
        return new InvitedBuilder();
    }

    public Invited build() {
        return new Invited(getJSONObject());
    }

    @Override
    protected InvitedBuilder self() {
        return this;
    }
}
