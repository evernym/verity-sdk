package com.evernym.verity.sdk.protocols.provision;
import com.evernym.verity.sdk.exceptions.VerityException;
import com.evernym.verity.sdk.protocols.provision.v0_7.ProvisionV0_7;

/**
 * Factory for the Provision protocol objects
 * <p/>
 *
 * The Provision protocol is used to provision an agent on the verity-application. This agent is the element that
 * represents an self-sovereign Identity (organization or otherwise). The protocol is unique that two ways. First it
 * only run once per agent. So it is a one-time operation to start things. If run again, it will create a new and
 * independent agent. Second, the interface for this protocol use an incomplete context object. It is during this
 * protocol that the context object is fully filled out.
 */
public class Provision {
    private Provision() {}

    /**
     * Constructor for the 1.0 Provision object. This constructor creates an object that is ready to create a new
     * agent.
     *
     * @return 0.7 Provision object
     */
    public static ProvisionV0_7 v0_7() {
        return new ProvisionImplV0_7();
    }

    /**
     * Constructor for the 1.0 Provision object. This constructor creates an object that is ready to create a new
     * agent.
     *
     * @param token an agent creation token that is provided by Evernym that authorize the creation of an agent
     * @return 0.7 Provision object
     * @throws VerityException when validating the token fails
     */
    public static ProvisionV0_7 v0_7(String token) throws VerityException {
        return new ProvisionImplV0_7(token);
    }
}
