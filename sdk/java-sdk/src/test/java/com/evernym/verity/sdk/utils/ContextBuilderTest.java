package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

import static com.evernym.verity.sdk.utils.ContextConstants.V_0_2;
import static org.junit.Assert.*;

public class ContextBuilderTest {

    @Test
    public void simpleBuild() throws Exception {
        String verkey1 = "9Wkz2i7yxrVSDFCBRzXDsaXnLMVFh7ZP3xv2ujPtaUJd";
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = ContextBuilder
                    .blank()
                    .walletConfig(testWallet)
                    .sdkVerKey(verkey1)
                    .build();

            c.closeWallet();
            assertEquals(verkey1, c.sdkVerKey());
        }
    }

    @Test
    public void multiBuild() throws Exception {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = ContextBuilder
                    .blank()
                    .walletConfig(testWallet)
                    .build();

            Wallet test = c.walletHandle();

            Context c2 = c.toContextBuilder()
                    .verityUrl("http://example.com")
                    .build();

            assert(c.walletHandle() == c2.walletHandle()); // Same wallet handle
            c2.closeWallet();
        }
    }

    @Test
    public void multiClosedBuild() throws Exception {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = ContextBuilder
                    .blank()
                    .walletConfig(testWallet)
                    .build();

            Wallet test = c.walletHandle();
            c.closeWallet();

            Context c2 = c.toContextBuilder()
                    .verityUrl("http://example.com")
                    .build();
            assert(test != c2.walletHandle()); // New wallet handle
            c2.closeWallet();
        }
    }

    @Test
    public void fromScratch() throws WalletException, UndefinedContextException {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Did testDid = new Did("CV65RFpeCtPu82hNF9i61G", "7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2");


            Context c = ContextBuilder.scratchContext(testWallet, "http://wwww.example.com", testDid, null);

            c.sdkVerKeyId();
            c.sdkVerKey();

            try {
                c.domainDID();
                fail( "Should throw UndefinedContextException" );
            } catch (UndefinedContextException ignored) {}

            try {
                c.verityAgentVerKey();
                fail( "Should throw UndefinedContextException" );
            } catch (UndefinedContextException ignored) {}

            assertEquals("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", c.verityPublicVerKey());

            c.closeWallet();
        }
    }

    @Test
    public void shouldCorrectlyParseConfig_0_1() {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        String endpointUrl = "http://localhost:4000";
        String verityUrl = "http://localhost:3000";
        Context context;
        try(TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("verityUrl", verityUrl);
            config.put("verityPublicDID", testWallet.getVerityPublicDID());
            config.put("verityPublicVerkey", testWallet.getVerityPublicVerkey());
            config.put("verityPairwiseDID", testWallet.getVerityPairwiseDID());
            config.put("verityPairwiseVerkey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkPairwiseDID", testWallet.getSdkPairwiseVerkey());
            config.put("sdkPairwiseVerkey", testWallet.getSdkPairwiseVerkey());
            config.put("endpointUrl", endpointUrl);
            context = ContextBuilder.fromJson(config).build();
            assertEquals(String.format("{\"id\":\"%s\"}", walletName), context.walletConfig().config());
            assertEquals(String.format("{\"key\":\"%s\"}", walletKey), context.walletConfig().credential());
            assertEquals(verityUrl, context.verityUrl());
            assertEquals(endpointUrl, context.endpointUrl());
            assertEquals(V_0_2, context.version());
            assertEquals(testWallet.getVerityPublicVerkey(), context.verityPublicVerKey());
            assertEquals(testWallet.getVerityPairwiseVerkey(), context.verityAgentVerKey());
            assertEquals(testWallet.getVerityPairwiseDID(), context.domainDID());
            assertEquals(testWallet.getSdkPairwiseVerkey(), context.sdkVerKey());
            assertNotNull(context.walletHandle());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void shouldCorrectlyParseConfig_0_2() {
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        String endpointUrl = "http://localhost:4000";
        String verityUrl = "http://localhost:3000";
        Context context;
        try(TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            JSONObject config = new JSONObject();
            config.put("walletName", walletName);
            config.put("walletKey", walletKey);
            config.put("endpointUrl", endpointUrl);
            config.put("verityUrl", verityUrl);
            config.put("verityPublicDID", testWallet.getVerityPublicDID());
            config.put("verityPublicVerKey", testWallet.getVerityPublicVerkey());
            config.put("domainDID", testWallet.getVerityPairwiseDID());
            config.put("verityAgentVerKey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkVerKeyId", testWallet.getSdkPairwiseVerkey());
            config.put("sdkVerKey", testWallet.getSdkPairwiseVerkey());
            config.put("version", V_0_2);
            context = ContextBuilder.fromJson(config).build();
            assertEquals(String.format("{\"id\":\"%s\"}", walletName), context.walletConfig().config());
            assertEquals(String.format("{\"key\":\"%s\"}", walletKey), context.walletConfig().credential());
            assertEquals(verityUrl, context.verityUrl());
            assertEquals(endpointUrl, context.endpointUrl());
            assertEquals(V_0_2, context.version());
            assertEquals(testWallet.getVerityPublicVerkey(), context.verityPublicVerKey());
            assertEquals(testWallet.getVerityPairwiseVerkey(), context.verityAgentVerKey());
            assertEquals(testWallet.getVerityPairwiseDID(), context.domainDID());
            assertEquals(testWallet.getSdkPairwiseVerkey(), context.sdkVerKey());
            assertNotNull(context.walletHandle());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
