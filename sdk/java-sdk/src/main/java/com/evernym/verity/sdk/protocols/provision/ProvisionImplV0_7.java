package com.evernym.verity.sdk.protocols.provision;

import com.evernym.verity.sdk.exceptions.ProvisionTokenException;
import com.evernym.verity.sdk.exceptions.UndefinedContextException;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.exceptions.WalletException;
import com.evernym.verity.sdk.protocols.AbstractProtocol;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;
import com.evernym.verity.sdk.transports.HTTPTransport;
import com.evernym.verity.sdk.utils.Context;
import com.evernym.verity.sdk.utils.Util;
import org.hyperledger.indy.sdk.IndyException;
import org.hyperledger.indy.sdk.crypto.Crypto;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.ExecutionException;

/*
 * NON_VISIBLE
 *
 * This is an implementation of ProvisionImplV0_7 but is not viable to user of Verity SDK. Created using the
 * static Provision class
 */
class ProvisionImplV0_7 extends AbstractProtocol implements ProvisionV0_7 {

    private String token;

    ProvisionImplV0_7() {}

    ProvisionImplV0_7(String token) throws VerityException {
        validateToken(token);
        this.token = token;
    }

    public void validateToken(String token) throws VerityException {
        JSONObject tokenObj = new JSONObject(token);
        byte[] data = (
            tokenObj.getString("nonce") +
            tokenObj.getString("timestamp") +
            tokenObj.get("sponseeId") +
            tokenObj.get("sponsorId")
        ).getBytes();

        try {
            boolean valid = Crypto.cryptoVerify(
                tokenObj.getString("sponsorVerKey"),
                data,
                Base64.getDecoder().decode(tokenObj.getString("sig"))
            ).get();
            
            //noinspection PointlessBooleanExpression
            if (valid == false) {
                throw new ProvisionTokenException("Invalid provision token -- signature does not validate");
            }
        } catch (IndyException e) {
            throw new ProvisionTokenException("Invalid provision token -- signature does not validate", e);
        } catch (InterruptedException | ExecutionException e) {
            throw new ProvisionTokenException("Unable to validate signature -- signature does not validate", e);
        }
    }

    protected JSONObject sendToVerity(Context context, byte[] packedMessage) throws WalletException, UndefinedContextException, IOException {
        HTTPTransport transport = new HTTPTransport();
        byte[] respBytes = transport.sendSyncMessage(context.verityUrl(), packedMessage);

        return Util.unpackMessage(context, respBytes);
    }

    public Context provision(Context context) throws IOException, UndefinedContextException, WalletException {
        if(context == null) {
            throw new UndefinedContextException("Context cannot be NULL");
        }

        JSONObject resp = sendToVerity(context, provisionMsgPacked(context));

        String domainDID = resp.getString("selfDID");
        String verityAgentVerKey = resp.getString("agentVerKey");

        return context.toContextBuilder()
                .domainDID(domainDID)
                .verityAgentVerKey(verityAgentVerKey)
                .build();
    }

    @Override
    public JSONObject provisionMsg(Context context) throws UndefinedContextException {
        if(context == null) {
            throw new UndefinedContextException("Context cannot be NULL");
        }

        JSONObject rtn = new JSONObject()
                .put("@id", AbstractProtocol.getNewId())
                .put("@type", messageType(CREATE_EDGE_AGENT))
                .put("requesterVk", context.sdkVerKey());
        
        if (token != null) {
            JSONObject tokenObj = new JSONObject(token);
            rtn.put("provisionToken", tokenObj);
        }
        
        return rtn;
    }

    @Override
    public byte[] provisionMsgPacked(Context context) throws UndefinedContextException, WalletException {
        if(context == null) {
            throw new UndefinedContextException("Context cannot be NULL");
        }

        return Util.packMessageForVerity(
            context.walletHandle(),
            provisionMsg(context),
            context.verityPublicDID(),
            context.verityPublicVerKey(),
            context.sdkVerKey(),
            context.verityPublicVerKey()
        );
    }
}
