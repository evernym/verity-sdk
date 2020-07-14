package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;

/**
 * Factory for the UpdateConfigs protocol objects
 * <p/>
 *
 * The UpdateConfigs protocol allow changes to the configuration of the registered agent on the verity-application.
 */
public class UpdateConfigs {
    /**
     * Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update
     * the endpoint.
     *
     * @param name Organization's name that is presented to third-parties
     * @param logoUrl A url to a logo that is presented to third-parties
     * @return 0.6 UpdateConfigs object
     */
    public static UpdateConfigsV0_6 v0_6(String name, String logoUrl) {
        return new UpdateConfigsImplV0_6(name, logoUrl);
    }

    /**
     * Constructor for the 0.6 UpdateEndpoint object. This constructor can only check the current configuration
     * (via status) of the protocol.
     *
     * @return 0.6 UpdateConfigs object
     */
    public static UpdateConfigsV0_6 v0_6() {
        return new UpdateConfigsImplV0_6();
    }
}
