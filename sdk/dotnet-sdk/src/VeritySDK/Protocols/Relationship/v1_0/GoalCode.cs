using System;
using System.ComponentModel.DataAnnotations;
using System.Json;
using System.Reflection;
using System.Runtime.CompilerServices;

namespace VeritySDK.Protocols.Relationship
{
    /// <summary>
    /// An enumeration of possible goals (reasons) for a relationship. 
    /// </summary>
    public enum GoalCode
    {
        /// <summary>
        /// To issue a credential
        /// </summary>
        [Display(Name = "issue-vc", Description = "To issue a credential")]
        ISSUE_VC,

        /// <summary>
        /// To request a proof
        /// </summary>
        [Display(Name = "request-proof", Description = "To request a proof")]
        REQUEST_PROOF,

        /// <summary>
        /// To create an account with a service
        /// </summary>
        [Display(Name = "create-account", Description = "To create an account with a service")]
        CREATE_ACCOUNT,

        /// <summary>
        /// To establish a peer-to-peer messaging relationship
        /// </summary>
        [Display(Name = "p2p-messaging", Description = "To establish a peer-to-peer messaging relationship")]
        P2P_MESSAGING
    }

    /// <summary>
    /// Extension for GoalCode enum
    /// </summary>
    public static class GoalCodeExtensions
    {
        /// <summary>
        /// Get Code for goal
        /// </summary>
        /// <param name="value">GoalCodeEnum</param>
        /// <returns></returns>
        public static string code(this GoalCode value)
        {
            return value.GetType()?
               .GetMember(value.ToString())?[0]?
               .GetCustomAttribute<DisplayAttribute>()?
               .Name;
        }

        /// <summary>
        /// Get Name for goal
        /// </summary>
        /// <param name="value">GoalCodeEnum</param>
        /// <returns></returns>
        public static string goalName(this GoalCode value)
        {
            return value.GetType()?
               .GetMember(value.ToString())?[0]?
               .GetCustomAttribute<DisplayAttribute>()?
               .Description;
        }
    }
}