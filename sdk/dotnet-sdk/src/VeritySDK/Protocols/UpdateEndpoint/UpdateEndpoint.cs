using System;
using System.Collections.Generic;
using System.Text;

namespace VeritySDK.Protocols.UpdateEndpoint
{
    /// <summary>
    /// Factory for the UpdateEndpoint protocol objects
    /// 
    /// The UpdateEndpoint protocol allow changes to the endpoint register with the verity-application agent. This
    /// endpoint is where agent sends signal messages. These messages are asynchronous and are sent via a http web hook.
    /// The endpoint is defined in the context. It must be set in the context before using this protocol.
    /// </summary>
    public class UpdateEndpoint
    {
        /// <summary>
        /// Constructor 
        /// </summary>
        private UpdateEndpoint() { }

        /// <summary>
        /// Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update the endpoint. 
        /// </summary>
        /// <returns>0.6 UpdateEndpoint object</returns>
        public static UpdateEndpointV0_6 v0_6()
        {
            return new UpdateEndpointV0_6();
        }

        /// <summary>
        /// Constructor for the 0.6 UpdateEndpoint object. This constructor creates an object that is ready to update the endpoint.
        /// </summary>
        /// <param name="authentication">Authentication for the endpoint url.</param>
        /// <returns>0.6 UpdateEndpoint object</returns>
        public static UpdateEndpointV0_6 v0_6(Authentication authentication)
        {
            return new UpdateEndpointV0_6(authentication);
        }
    }
}
