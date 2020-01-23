package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestWallet;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.json.JSONObject;
import org.junit.Test;

import java.util.UUID;

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
                    .sdkPairwiseVerkey(verkey1)
                    .build();

            c.closeWallet();
            assertEquals(verkey1, c.sdkPairwiseVerkey());
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


            Context c = ContextBuilder.scratchContext(testWallet, "http://wwww.example.com", testDid);

            c.sdkPairwiseDID();
            c.sdkPairwiseVerkey();

            try {
                c.verityPairwiseDID();
                fail( "Should throw UndefinedContextException" );
            } catch (UndefinedContextException ignored) {}

            try {
                c.verityPairwiseVerkey();
                fail( "Should throw UndefinedContextException" );
            } catch (UndefinedContextException ignored) {}

            assertEquals("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", c.verityPublicVerkey());

            c.closeWallet();
        }
    }

    @Test
    public void shouldCorrectlyParseConfig() {
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
            assertEquals(testWallet.getVerityPublicVerkey(), context.verityPublicVerkey());
            assertEquals(testWallet.getVerityPairwiseVerkey(), context.verityPairwiseVerkey());
            assertEquals(testWallet.getVerityPairwiseDID(), context.verityPairwiseDID());
            assertEquals(testWallet.getSdkPairwiseVerkey(), context.sdkPairwiseVerkey());
            assertEquals(endpointUrl, context.endpointUrl());
            assertNotNull(context.walletHandle());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
