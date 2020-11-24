using System;

namespace VeritySDK.Protocols.Provision
{
    /// <summary>
    /// Factory for the Provision protocol objects
    /// 
    /// The Provision protocol is used to provision an agent on the verity-application. This agent is the element that
    /// represents an self-sovereign Identity (organization or otherwise). The protocol is unique that two ways. First it
    /// only run once per agent. So it is a one-time operation to start things. If run again, it will create a new and
    /// independent agent. Second, the interface for this protocol use an incomplete context object. It is during this
    /// protocol that the context object is fully filled out.
    /// </summary>
    public class Provision
    {
        private Provision() { }

        /// <summary>
        /// Constructor for the 1.0 Provision object. This constructor creates an object that is ready to create a new agent.
        /// </summary>
        /// <returns>0.7 Provision object</returns>
        public static ProvisionV0_7 v0_7()
        {
            return new ProvisionV0_7();
        }

        /// <summary>
        /// Constructor for the 1.0 Provision object. This constructor creates an object that is ready to create a new agent. 
        /// </summary>
        /// <param name="token">token an agent creation token that is provided by Evernym that authorize the creation of an agent</param>
        /// <returns>0.7 Provision object</returns>
        public static ProvisionV0_7 v0_7(String token)
        {
            return new ProvisionV0_7(token);
        }
    }
}