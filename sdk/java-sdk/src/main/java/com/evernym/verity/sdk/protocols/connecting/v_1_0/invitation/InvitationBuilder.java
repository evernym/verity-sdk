package com.evernym.verity.sdk.protocols.connecting.v_1_0.invitation;

import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONArray;

import java.util.ArrayList;

import static com.evernym.verity.sdk.utils.JsonUtil.makeArray;

public class InvitationBuilder extends BaseMsgBuilder<InvitationBuilder> implements AsJsonObject  {

    public static InvitationBuilder blank() {
        return new InvitationBuilder();
    }

    public InvitationBuilder did(String did) {
        addToJSON("did", did);
        return this;
    }

    public InvitationBuilder label(String label) {
        addToJSON("label", label);
        return this;
    }

    public InvitationBuilder serviceEndpoint(String serviceEndpoint) {
        addToJSON("serviceEndpoint", serviceEndpoint);
        return this;
    }

    public InvitationBuilder recipientKeys(ArrayList<String> recipientKeys) {
        addToJSON("recipientKeys", new JSONArray(recipientKeys));
        return this;
    }

    public InvitationBuilder routingKeys(ArrayList<String> routingKeys) {
        addToJSON("routingKeys", new JSONArray(routingKeys));
        return this;
    }

    public Invitation build() {
        return new Invitation(getJSONObject());
    }

    @Override
    protected InvitationBuilder self() {
        return this;
    }
}
