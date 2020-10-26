using System;
using System.Json;

namespace VeritySDK
{
    /**
     * Factory for the UpdateConfigs protocol objects
     * <p/>
     *
     * The UpdateConfigs protocol allow changes to the configuration of the registered agent on the verity-application.
     */
    public class UpdateConfigs
    {
        /**
         * Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update
         * the endpoint.
         *
         * @param name Organization's name that is presented to third-parties
         * @param logoUrl A url to a logo that is presented to third-parties
         * @return 0.6 UpdateConfigs object
         */
        public static UpdateConfigsV0_6 v0_6(string name, string logoUrl)
        {
            return new UpdateConfigsImplV0_6(name, logoUrl);
        }

        /**
         * Constructor for the 0.6 UpdateEndpoint object. This constructor can only check the current configuration
         * (via status) of the protocol.
         *
         * @return 0.6 UpdateConfigs object
         */
        public static UpdateConfigsV0_6 v0_6()
        {
            return new UpdateConfigsImplV0_6();
        }
    }
}