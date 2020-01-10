package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestWallet;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.WalletException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.junit.Test;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ContextBuilderTest {

    @Test
    public void simpleBuild() throws Exception {
        String verkey1 = "9Wkz2i7yxrVSDFCBRzXDsaXnLMVFh7ZP3xv2ujPtaUJd";
        String walletName = UUID.randomUUID().toString();
        String walletKey = UUID.randomUUID().toString();
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = new ContextBuilder()
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
            Context c = new ContextBuilder()
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
            Context c = new ContextBuilder()
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
            } catch (UndefinedContextException expectedException) {}

            try {
                c.verityPairwiseVerkey();
                fail( "Should throw UndefinedContextException" );
            } catch (UndefinedContextException expectedException) {}

            assertEquals("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", c.verityPublicVerkey());

            c.closeWallet();
        }
    }
}
