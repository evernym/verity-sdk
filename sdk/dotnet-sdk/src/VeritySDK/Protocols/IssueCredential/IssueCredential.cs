using System;
using System.Collections.Generic;
using System.Text;

namespace VeritySDK.Protocols.IssueCredential
{
    /// <summary>
    /// Factory for IssueCredential protocol objects.
    /// 
    /// The IssueCredential protocol allows one self-sovereign party to issue a verifiable credential to another
    /// self-sovereign party.On the verity-application, these protocols use `anoncreds` as defined in the
    /// Hyperledger-indy project. In the verity-sdk, the interface presented by these objects allow key-value attributes
    /// to be issued to parties that have a DID-comm channel.
    /// </summary>
    public class IssueCredential
    {
        /// <summary>
        /// Constructor for the 1.0 IssueCredential object. This constructor creates an object that is ready to start process of issuing a credential.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="credDefId">the Credential Definition that will be used to issue the credential</param>
        /// <param name="values">a map of key-value pairs that make up the attributes in the credential</param>
        /// <param name="comment">a human readable comment that is presented before issuing the credential</param>
        /// <param name="price">token price (NOT CURRENTLY USED)</param>
        /// <param name="autoIssue">flag for automatically issuing credential after receiving response for receiver (skip getting signal for credential request and waiting for issue control message)</param>
        /// <param name="byInvitation">flag to create out-of-band invitation as a part of the IssueCredential protocol</param>
        /// <returns>1.0 IssueCredential object</returns>
        public static IssueCredentialV1_0 v1_0(
                string forRelationship,
                string credDefId,
                Dictionary<string, string> values,
                string comment,
                string price,
                bool? autoIssue,
                bool byInvitation)
        {
            return new IssueCredentialV1_0(
                    forRelationship,
                    credDefId,
                    values,
                    comment,
                    price,
                    autoIssue,
                    byInvitation);
        }

        /// <summary>
        /// Constructor for the 1.0 IssueCredential object. This constructor creates an object that is ready to start process of issuing a credential.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="credDefId">the Credential Definition that will be used to issue the credential</param>
        /// <param name="values">a map of key-value pairs that make up the attributes in the credential</param>
        /// <param name="comment">a human readable comment that is presented before issuing the credential</param>
        /// <param name="price">token price (NOT CURRENTLY USED)</param>
        /// <param name="autoIssue">flag for automatically issuing credential after receiving response for receiver (skip getting signal for credential request and waiting for issue control message)</param>
        /// <returns>1.0 IssueCredential object</returns>
        public static IssueCredentialV1_0 v1_0(
                string forRelationship,
                string credDefId,
                Dictionary<string, string> values,
                string comment,
                string price,
                bool? autoIssue)
        {
            return new IssueCredentialV1_0(
                    forRelationship,
                    credDefId,
                    values,
                    comment,
                    price,
                    autoIssue);
        }

        /// <summary>
        /// Constructor for the 1.0 IssueCredential object. This constructor creates an object that is ready to start process of issuing a credential.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="credDefId">the Credential Definition that will be used to issue the credential</param>
        /// <param name="values">a map of key-value pairs that make up the attributes in the credential</param>
        /// <param name="comment">a human readable comment that is presented before issuing the credential</param>
        /// <param name="price">token price (NOT CURRENTLY USED)</param>
        /// <returns>1.0 IssueCredential object</returns>
        public static IssueCredentialV1_0 v1_0(
                string forRelationship,
                string credDefId,
                Dictionary<string, string> values,
                string comment,
                string price)
        {
            return new IssueCredentialV1_0(
                    forRelationship,
                    credDefId,
                    values,
                    comment,
                    price,
                    false);
        }

        /// <summary>
        /// Constructor for the 1.0 IssueCredential object. This constructor re-creates an object from a known relationship and threadId. This object can only check status of the protocol.
        /// </summary>
        /// <param name="forRelationship">the relationship identifier (DID) for the pairwise relationship that will be used</param>
        /// <param name="threadId">the thread id of the already started protocol</param>
        /// <returns>1.0 IssueCredential object</returns>
        public static IssueCredentialV1_0 v1_0(
                string forRelationship,
                string threadId)
        {
            return new IssueCredentialV1_0(
                    forRelationship,
                    threadId
            );
        }

    }
}
