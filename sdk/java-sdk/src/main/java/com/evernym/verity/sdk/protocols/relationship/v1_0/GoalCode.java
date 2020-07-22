package com.evernym.verity.sdk.protocols.relationship.v1_0;

/**
 * An enumeration of possible goals (reasons) for a relationship.
 *
 * @see <a href="https://github.com/hyperledger/aries-rfcs/blob/master/features/0434-outofband/README.md" target="_blank" rel="noopener noreferrer">Aries 0434: Connection Protocol</a>
 */
public enum GoalCode {

    /**
     * To issue a credential
     */
    ISSUE_VC("issue-vc", "To issue a credential"),
    /**
     * To request a proof
     */
    REQUEST_PROOF("request-proof", "To request a proof"),
    /**
     * To create an account with a service
     */
    CREATE_ACCOUNT("create-account", "To create an account with a service"),
    /**
     * To establish a peer-to-peer messaging relationship
     */
    P2P_MESSAGING("p2p-messaging", "To establish a peer-to-peer messaging relationship");

    private final String code;
    private final String goalName;

    /**
     * Goal code is a tuple with a short code string and human readable name string
     * @param code short code string
     * @param name human readable string
     */
    GoalCode(String code, String name) {
        this.code = code;
        this.goalName = name;
    }

    /**
     * @return the defined short code string
     */
    public String code() {
        return code;
    }

    /**
     * @return @return the defined human readable name string
     */
    public String goalName() {
        return goalName;
    }

}
