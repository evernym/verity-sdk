using System;
using System.Collections.Generic;
using System.Text;

namespace VeritySDK.Protocols.BasicMessage
{
    /// <summary>
    /// Factory for BasicMessage protocol objects.
    ///
    /// The IssueCredential protocol allows one self-sovereign party to send text messages to another
    /// self-sovereign party.
    /// </summary>
    public class BasicMessage
    {
        private BasicMessage() { }

        /// <summary>
        /// Constructor for the 1.0 BasicMessage object. This constructor creates an object that is ready to send the given message
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="content">The main text of the message </param>
        /// <param name="sentTime">The time the message was sent.</param>
        /// <param name="localization">Language localization code</param>
        /// <returns>1.0 BasicMessage object</returns>
        public static BasicMessageV1_0 v1_0(string forRelationship,
            string content,
            string sentTime,
            string localization)
        {
            return new BasicMessageV1_0(forRelationship, content, sentTime, localization);
        }
    }
}
