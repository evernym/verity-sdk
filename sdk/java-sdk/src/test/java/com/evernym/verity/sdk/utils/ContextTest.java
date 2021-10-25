package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.vdrtools.IndyException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.evernym.verity.sdk.TestHelpers.assertEqualsJSONObject;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ContextTest {

    // TODO: Add more robust tests with bad configs.

    @Test
    public void toJson_0_1() {
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
            assertEqualsJSONObject(withNewKeys(config.put("version", "0.2")), context.toJson());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void toJson_0_2() {
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
            config.put("verityPublicVerKey", testWallet.getVerityPublicVerkey());
            config.put("domainDID", testWallet.getVerityPairwiseDID());
            config.put("verityAgentVerKey", testWallet.getVerityPairwiseVerkey());
            config.put("sdkVerKeyId", testWallet.getSdkPairwiseVerkey());
            config.put("sdkVerKey", testWallet.getSdkPairwiseVerkey());
            config.put("endpointUrl", endpointUrl);
            config.put("version", "0.2");
            context = ContextBuilder.fromJson(config).build();
            assertEqualsJSONObject(withNewKeys(config), context.toJson());

            context.closeWallet();
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void restApiToken() throws VerityException, IndyException {
        Context c = TestHelpers.getContext("000000000000000000000000Team1VAS");
        String t = c.restApiToken();
        String e = "HZ3Ak6pj9ryFASKbA9fpwqjVh42F35UDiCLQ13J58Xoh" +
                ":4Wf6JtGy9enwwXVKcUgADPq7Pnf9T2YZ8LupMEVxcQQf98uuRYxWGHLAwXWp8DtaEYHo4cUeExDjApMfvLJQ48Kp";
        assertEquals(e, t);

    }

    private JSONObject withNewKeys(JSONObject jsonObject) {
        JSONObject config = new JSONObject();
        jsonObject.toMap().forEach((s, o) ->
            config.put(getNewKey(s), o));
        return config;
    }

    private String getNewKey(String key) {
        switch (key) {
            case "verityPublicVerkey"       : return "verityPublicVerKey";
            case "verityPairwiseDID"        : return "domainDID";
            case "verityPairwiseVerkey"     : return "verityAgentVerKey";
            case "sdkPairwiseVerkey"        : return "sdkVerKey";
            case "sdkPairwiseDID"           : return "sdkVerKeyId";
            default                         : return key;
        }
    }
}