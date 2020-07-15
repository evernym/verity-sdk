package com.evernym.verity.sdk.protocols.relationship.v1_0;

public enum GoalCode {

    ISSUE_VC("issue-vc", "To issue a credential"),
    REQUEST_PROOF("request-proof", "To request a proof"),
    CREATE_ACCOUNT("create-account", "To create an account with a service"),
    P2P_MESSAGING("p2p-messaging", "To establish a peer-to-peer messaging relationship");

    private final String code;
    private final String goalName;

    GoalCode(String code, String name) {
        this.code = code;
        this.goalName = name;
    }

    public String code() {
        return code;
    }

    public String goalName() {
        return goalName;
    }

}
