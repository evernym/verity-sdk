package com.evernym.verity.sdk.utils;

import com.evernym.verity.sdk.protocols.MessageFamily;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MsgFamilyBuilder {

    static Pattern p = Pattern.compile("(did:.*:.*);spec/(.*)/(.*)/(.*)");

    public static MessageFamily fromQualifiedMsgType(String qualifiedMessageType) {
        Matcher m = p.matcher(qualifiedMessageType);
        if (m.find()) {
            String qualifier = m.group(1);
            String familyName = m.group(2);
            String familyVersion = m.group(3);

            return new MessageFamily() {
                @Override
                public String qualifier() { return qualifier;}

                @Override
                public String family() { return familyName; }

                @Override
                public String version() { return familyVersion; }
            };
        }
        else {
            throw new IllegalStateException(String.format("Invalid message type field, unable to parse Message Family for '%s'", qualifiedMessageType));
        }

    }
}
