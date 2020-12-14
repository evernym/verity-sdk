package com.evernym.verity.sdk.protocols.basicmessage;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.basicmessage.v1_0.BasicMessageV1_0;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.DbcUtil;
import org.json.JSONObject;

import java.io.IOException;

/*
 * NON_VISIBLE
 *
 * This is an implementation of BasicMessageImplV1_0 but is not viable to user of Verity SDK. Created using the
 * static BasicMessage class
 */
class BasicMessageImplV1_0 extends AbstractProtocol implements BasicMessageV1_0 {

    final String forRelationship;
    String content;
    String sent_time;
    String localization;

    public static final String BASIC_MESSAGE = "send-message";

    BasicMessageImplV1_0(String forRelationship,
                            String content,
                            String sent_time,
                            String localization) {
        super();
        DbcUtil.requireNotNull(forRelationship, "forRelationship");

        this.forRelationship = forRelationship;
        this.content = content;
        this.sent_time = sent_time;
        this.localization = localization;
    }

    public void message(Context context) throws IOException, VerityException {
        send(context, messageMsg(context));
    }

    public JSONObject messageMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", messageType(BASIC_MESSAGE));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);

        msg.put("content", this.content);
        msg.put("sent_time", this.sent_time);
        msg.put("~l10n", new JSONObject().put("locale", this.localization));
        return msg;
    }

    public byte[] messageMsgPacked(Context context) throws VerityException {
        return packMsg(context, messageMsg(context));
    }
}
