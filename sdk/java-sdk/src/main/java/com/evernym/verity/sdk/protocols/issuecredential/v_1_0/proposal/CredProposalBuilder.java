package com.evernym.verity.sdk.protocols.issuecredential.v_1_0.proposal;

import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.common.CredRestrictionBuilder;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewAttribute;
import com.evernym.verity.sdk.protocols.issuecredential.v_1_0.cred_preview.CredPreviewBuilder;
import org.json.JSONObject;
import java.util.List;

public class CredProposalBuilder extends CredRestrictionBuilder<CredProposalBuilder> {

    public static CredProposalBuilder blank() {
        return new CredProposalBuilder();
    }

    public CredProposalBuilder comment(String comment) {
        addToJSON("comment", comment);
        return this;
    }

    public CredProposalBuilder proposal(List<CredPreviewAttribute> attributes, MessageFamily msgFamily) {
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
    protected CredProposalBuilder self() {
        return this;
    }

    public CredProposal build() {
        return new CredProposal(getJSONObject());
    }
}
