package com.evernym.verity.sdk.protocols.updateconfigs;

import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsImplV0_6;
import com.evernym.verity.sdk.protocols.updateconfigs.v0_6.UpdateConfigsV0_6;

public class UpdateConfigs {
    public static UpdateConfigsV0_6 v0_6(String name, String logoUrl) { return new UpdateConfigsImplV0_6(name, logoUrl); }
}
