using System;
using System.ComponentModel.DataAnnotations;
using System.Json;
using System.Reflection;
using System.Runtime.CompilerServices;

namespace VeritySDK
{
    /**
     * An enumeration of possible goals (reasons) for a relationship.
     *
     * @see <a href="https://github.com/hyperledger/aries-rfcs/blob/master/features/0434-outofband/README.md" target="_blank" rel="noopener noreferrer">Aries 0434: Connection Protocol</a>
     */

    public enum GoalCode
    {
        /**
         * To issue a credential
         */
        [Display(Name = "issue-vc", Description = "To issue a credential")]
        ISSUE_VC,
        /**
         * To request a proof
         */
        [Display(Name = "request-proof", Description = "To request a proof")]
        REQUEST_PROOF,
        /**
         * To create an account with a service
         */
        [Display(Name = "create-account", Description = "To create an account with a service")]
        CREATE_ACCOUNT,
        /**
         * To establish a peer-to-peer messaging relationship
         */
        [Display(Name = "p2p-messaging", Description = "To establish a peer-to-peer messaging relationship")]
        P2P_MESSAGING
    }


    public static class GoalCodeExtensions
    {
        public static string code(this GoalCode value)
        {
            return value.GetType()?
               .GetMember(value.ToString())?[0]?
               .GetCustomAttribute<DisplayAttribute>()?
               .Name;
        }

        public static string goalName(this GoalCode value)
        {
            return value.GetType()?
               .GetMember(value.ToString())?[0]?
               .GetCustomAttribute<DisplayAttribute>()?
               .Description;
        }
    }
}