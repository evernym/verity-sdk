using System;
using System.Json;

namespace VeritySDK.Protocols.UpdateConfigs
{
    /// <summary>
    /// Factory for the UpdateConfigs protocol objects
    /// The UpdateConfigs protocol allow changes to the configuration of the registered agent on the verity-application.
    /// </summary>
    public class UpdateConfigs
    {
        /// <summary>
        /// Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update the endpoint. 
        /// </summary>
        /// <param name="name">Organization's name that is presented to third-parties</param>
        /// <param name="logoUrl">A url to a logo that is presented to third-parties</param>
        /// <returns>0.6 UpdateConfigs object</returns>
        public static UpdateConfigsV0_6 v0_6(string name, string logoUrl)
        {
            return new UpdateConfigsV0_6(name, logoUrl);
        }

        /// <summary>
        /// Constructor for the 0.6 UpdateEndpoint object. This constructor can only check the current configuration (via status) of the protocol.
        /// </summary>
        /// <returns>0.6 UpdateConfigs object</returns>
        public static UpdateConfigsV0_6 v0_6()
        {
            return new UpdateConfigsV0_6();
        }
    }
}