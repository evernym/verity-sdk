package com.evernym.verity.sdk.protocols.relationship.v_1_0.create_key;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;

public class CreateKeyBuilder extends BaseMsgBuilder<CreateKeyBuilder> implements AsJsonObject  {

    public static CreateKeyBuilder blank() {
        return new CreateKeyBuilder();
    }

    public CreateKey build() {
        return new CreateKey(getJSONObject());
    }

    @Override
    protected CreateKeyBuilder self() {
        return this;
    }
}
