package com.evernym.verity.sdk.protocols;

import com.evernym.verity.sdk.TestHelpers;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.updateconfigs.UpdateConfigs;
import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.evernym.verity.sdk.TestHelpers.unpackForwardMessage;
import static org.junit.Assert.*;

public class UpdateConfigsTest {

    private final String name = "Name1";
    private final String logoUrl = "http://logo.url";

    @Test
    public void testGetMessageType() {
        UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);
        String msgName = "msg name";
        assertEquals(Util.getMessageType(
                Util.EVERNYM_MSG_QUALIFIER,
                testProtocol.family(),
                testProtocol.version(),
                msgName
        ), testProtocol.messageType(msgName));
    }

    @Test
    public void testConstructorWithAttr() throws VerityException {
        Context context = TestHelpers.getContext();
        UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);

        JSONObject msg = testProtocol.updateMsg(context);
        testUpdateMsgMessages(msg);

        JSONObject msg2 = testProtocol.statusMsg(context);
        testStatusMsg(msg2);
    }

    @Test
    public void testConstructorWithoutAttr() throws VerityException {
        Context context = TestHelpers.getContext();
        UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6();

        JSONObject msg2 = testProtocol.statusMsg(context);
        testStatusMsg(msg2);
    }

    private void testUpdateMsgMessages(JSONObject requestMsg) {
        Map<String, String> expectedConfigs = new HashMap<>();
        expectedConfigs.put("name", this.name);
        expectedConfigs.put("logoUrl", this.logoUrl);

        assertEquals(
                "did:sov:123456789abcdefghi1234;spec/update-configs/0.6/update",
                requestMsg.getString("@type")
        );
        assertNotNull(requestMsg.getString("@id"));
        assertEquals(
                parseConfigs(requestMsg.getJSONArray("configs")),
                expectedConfigs
        );
    }

    private void testStatusMsg(JSONObject statusMsg) {
        assertEquals(
                "did:sov:123456789abcdefghi1234;spec/update-configs/0.6/get-status",
                statusMsg.getString("@type"));
        assertNotNull(statusMsg.getString("@id"));
    }

    @Test
    public void testUpdate() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6(name, logoUrl);
            byte [] message = testProtocol.updateMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testUpdateMsgMessages(unpackedMessage);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    @Test
    public void testGetStatus() throws Exception {
        Context context = null;
        try {
            context = TestHelpers.getContext();
            UpdateConfigsV0_6 testProtocol = UpdateConfigs.v0_6();
            byte [] message = testProtocol.statusMsgPacked(context);
            JSONObject unpackedMessage = unpackForwardMessage(context, message);
            testStatusMsg(unpackedMessage);
        } catch(Exception e) {
            e.printStackTrace();
            fail();
        } finally {
            TestHelpers.cleanup(context);
        }
    }

    private Map parseConfigs(JSONArray configsJson) {
        Map<String, String> configs = new HashMap<>();
        for(int i = 0; i < configsJson.length(); i++) {
            JSONObject c = configsJson.getJSONObject(i);
            configs.put(c.getString("name"), c.getString("value"));
        }

        return configs;
    }
}