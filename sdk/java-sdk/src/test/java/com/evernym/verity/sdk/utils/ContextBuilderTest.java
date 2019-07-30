package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestWallet;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import org.hyperledger.indy.sdk.wallet.Wallet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ContextBuilderTest {

    @Test
    public void simpleBuild() throws Exception {
        String walletName = "test1";
        String walletKey = "test1";
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = new ContextBuilder()
                    .walletName(walletName)
                    .walletKey(walletKey)
                    .build();

            c.closeWallet();
            assertEquals(walletName, c.walletName());
        }
    }

    @Test
    public void multiBuild() throws Exception {
        String walletName = "test1";
        String walletKey = "test1";
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = new ContextBuilder()
                    .walletName(walletName)
                    .walletKey(walletKey)
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
        String walletName = "test1";
        String walletKey = "test1";
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Context c = new ContextBuilder()
                    .walletName(walletName)
                    .walletKey(walletKey)
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
    public void fromScratch() throws Exception {
        String walletName = "test1";
        String walletKey = "test1";
        try (TestWallet testWallet = new TestWallet(walletName, walletKey)) {
            Did testDid = new Did("CV65RFpeCtPu82hNF9i61G", "7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2");


            Context c = ContextBuilder._scratchContext(walletName, walletKey, "http://wwww.example.com", testDid);

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
