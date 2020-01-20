package com.evernym.verity.sdk.protocols.questionanswer;

import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.MessageFamily;
import com.evernym.verity.sdk.protocols.Protocol;
import com.evernym.verity.sdk.utils.Context;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Builds and sends a new encrypted agent message for the Question protocol.
 */
public abstract class AskCommonImpl extends Protocol implements MessageFamily {

    String forRelationship;
    String questionText;
    String questionDetail;
    String[] validResponses;
    String answer;
    private boolean signatureRequired;

    public static String ASK_QUESTION = "ask-question";
    public static String ANSWER_QUESTION = "answer-question";
    public static String GET_STATUS = "get-status";


    public AskCommonImpl(String forRelationship,
                         String questionText,
                         String questionDetail,
                         String[] validResponses) {
        this(forRelationship, questionText, questionDetail, validResponses, true);
    }

    public AskCommonImpl(String forRelationship,
                         String questionText,
                         String questionDetail,
                         String[] validResponses,
                         Boolean signatureRequired
    ) {
        super();
        this.forRelationship = forRelationship;
        this.questionText = questionText;
        this.questionDetail = questionDetail;
        this.validResponses = validResponses;
        this.signatureRequired = signatureRequired;
    }

    public AskCommonImpl(String forRelationship, String threadId, String answer) {
        super(threadId);
        this.answer = answer;
    }

    public void ask(Context context) throws IOException, VerityException {
        send(context, askMsg(context));
    }

    public JSONObject askMsg(Context context) {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(ASK_QUESTION));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        msg.put("text", this.questionText);
        msg.put("detail", this.questionDetail);
        msg.put("valid_responses", this.validResponses);
        msg.put("signature_required", this.signatureRequired);
        return msg;
    }

    public byte[] askMsgPacked(Context context) throws VerityException {
        return packMsg(context, askMsg(context));
    }


    public void answer(Context context) throws IOException, VerityException {
        send(context, answerMsg(context));
    }

    public JSONObject answerMsg(Context context) throws VerityException {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(ANSWER_QUESTION));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        msg.put("response", this.answer);
        return msg;
    }

    public byte[] answerMsgPacked(Context context) throws VerityException {
        return  packMsg(context, answerMsg(context));
    }

    public void status(Context context) throws IOException, VerityException {
        send(context, statusMsg(context));
    }

    public JSONObject statusMsg(Context context) throws VerityException {
        JSONObject msg = new JSONObject();
        msg.put("@type", getMessageType(GET_STATUS));
        msg.put("@id", getNewId());
        addThread(msg);
        msg.put("~for_relationship", this.forRelationship);
        return msg;
    }

    public byte[] statusMsgPacked(Context context) throws VerityException {
        return packMsg(context, statusMsg(context));
    }
}