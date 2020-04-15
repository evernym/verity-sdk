//package com.evernym.verity.sdk.protocols.relationship.v1_0;
//
//import com.evernym.verity.sdk.protocols.common.BaseMsgBuilder;
////import com.evernym.verity.sdk.protocols.connecting.v1_0.Invitation;
//import com.evernym.verity.sdk.utils.AsJsonObject;
//import org.hyperledger.indy.sdk.StringUtils;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.util.List;
//
//public class PrepareInvitationBuilder extends BaseMsgBuilder<PrepareInvitationBuilder> implements AsJsonObject  {
//
//    public static PrepareInvitationBuilder blank() {
//        return new PrepareInvitationBuilder();
//    }
//
//    public PrepareInvitationBuilder did(String did) {
//        if(!StringUtils.isNullOrWhiteSpace(did)) {
//            addToJSON("did", did);
//        }
//        return this;
//    }
//
//    public PrepareInvitationBuilder label(String label) {
//        addToJSON("label", label);
//        return this;
//    }
//
//    public PrepareInvitationBuilder recipientKeys(List<String> recipientKeys) {
//        addToJSON("recipientKeys", new JSONArray(recipientKeys));
//        return this;
//    }
//
//    public PrepareInvitationBuilder routingKeys(List<String> routingKeys) {
//        addToJSON("routingKeys", new JSONArray(routingKeys));
//        return this;
//    }
//
//    public JSONObject build() {
//        return getJSONObject();
//    }
//
//    @Override
//    protected PrepareInvitationBuilder self() {
//        return this;
//    }
//}
