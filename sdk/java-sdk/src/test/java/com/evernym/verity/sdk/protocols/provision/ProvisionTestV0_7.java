package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class ProvisionTestV0_7 {

    @Test
    public void createMessage() throws VerityException {
        Context context = TestHelpers.getContext();

        ProvisionV0_7 p = Provision.v0_7();
        JSONObject msg = p.provisionMsg(context);
        assertEquals(msg.getString("requesterVk"), context.sdkVerKey());


        String goodToken = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
        ProvisionV0_7 p2 = Provision.v0_7(goodToken);

        JSONObject msg2 = p2.provisionMsg(context);
        assertTrue(msg2.has("provisionToken"));
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
    
    @Test
    public void validateToken() throws VerityException {
        String testToken = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"ZkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
        new ProvisionImplV0_7().validateToken(testToken);

        try {
            String testToken2 = "{\"sponseeId\": \"myId\", \"sponsorId\": \"evernym-test-sponsorabc123\", \"nonce\": \"123\", \"timestamp\": \"2020-06-05T21:33:36.085Z\", \"sig\": \"AkejifRr3txh7NrKokC5l2D2YcABUlGlAoFHZD9RapHHBfVtNnHgYux1RCAiEh4Q31VJE3C92T1ZnqDm1WlEAA==\", \"sponsorVerKey\": \"GJ1SzoWzavQYfNL9XkaJdrQejfztN4XqdsiV4ct3LXKL\"}";
            new ProvisionImplV0_7().validateToken(testToken2);
            fail();
        }
        catch (VerityException ignored) {}
    }


}
