package com.evernym.verity.sdk.protocols.writecreddef.v0_6;

import com.evernym.verity.sdk.utils.AsJsonObject;
import org.json.JSONObject;

/**
 * Holder of revocation registry configuration. This class holds a JSON object and is not intended to be created
 * directly. The versioned WriteCredentialDefinition interface provides helper functions to create this holder
 * class.
 *
 * @see WriteCredentialDefinitionV0_6#disabledRegistryConfig()
 */
public class RevocationRegistryConfig implements AsJsonObject {
    private final JSONObject data;

    /**
     * Constructs the object with the given JSON object
     *
     * @param data the given configuration as expressed in JSON object
     */
    public RevocationRegistryConfig(JSONObject data) {
        this.data = data;
    }

    /**
     * Convert this object to a JSON object
     *
     * @return this object as a JSON object
     */
    @Override
    public JSONObject toJson() {
        return data;
    }
}
