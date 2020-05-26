package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class ProvisionTestV0_7 {

    @Test
    public void createMessage() throws WalletException, UndefinedContextException {
        Context context = TestHelpers.getContext();

        ProvisionV0_7 p = Provision.v0_7();
        JSONObject msg = p.provisionMsg(context);
        assertEquals(msg.getString("requesterVk"), context.sdkVerKey());
    }

    @Test
    public void processMessage() throws WalletException, IOException, UndefinedContextException {
        JSONObject returnMessage = new JSONObject()
                .put("selfDID", "4ut8tgCBdUMCYZrJh5JS4o")
                .put("agentVerKey", "38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE");
        Context context = TestHelpers.getContext();

        ProvisionV0_7 p = new ProvisionImplV0_7() {
            @Override
            protected JSONObject sendToVerity(Context context, byte[] packedMessage) {
                return returnMessage;
            }
        };

        Context context2 = p.provision(context);
        assertEquals("4ut8tgCBdUMCYZrJh5JS4o", context2.domainDID());
        assertEquals("38fBEUX2sUQF7ZsY6epz1cbCvfBfDbQghUZUzFP6JChE", context2.verityAgentVerKey());
    }
}
