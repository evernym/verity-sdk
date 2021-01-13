using System;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Utilities for design by contract. Allows for common pre-condition and post-condition checking.
    /// </summary>
    public class DbcUtil
    {
        /// <summary>
        /// Checks and throws an ArgumentException if the given object is null
        /// </summary>
        /// <param name="arg">any object that can be null</param>
        public static void requireNotNull(object arg)
        {
            requireNotNull(arg, "ARG");
        }

        /// <summary>
        /// Checks and throws an ArgumentException if the given object is null
        /// </summary>
        /// <param name="arg">any object that can be null</param>
        /// <param name="argName">name of the argument being checked, used in exception message if null</param>
        public static void requireNotNull(object arg, string argName)
        {
            require(arg != null, "required that " + argName + " must NOT be null");
        }

        /// <summary>
        /// Checks that given boolean is true, throws an ArgumentException if false
        /// </summary>
        /// <param name="requirement">requirement testable requirement</param>
        public static void require(bool requirement)
        {
            require(requirement, "");
        }

        /// <summary>
        /// Checks that given boolean is true, throws an ArgumentException if false
        /// </summary>
        /// <param name="requirement">requirement testable requirement</param>
        /// <param name="msg">string that is used to build exception message</param>
        public static void require(bool requirement, string msg)
        {
            if (!requirement)
            {
                throw new ArgumentException("requirement failed: " + msg);
            }
        }

        /// <summary>
        /// Checks and throws an ArgumentException if the given object is null
        /// </summary>
        /// <param name="arg">string argument which needs to be checked</param>
        /// <param name="argName">name of the argument being checked, used in exception message</param>
        public static void requireStringNotNullOrEmpty(string arg, string argName)
        {
            require(!String.IsNullOrEmpty(arg), "required that " + argName + " must NOT be null or empty string");
        }

        /// <summary>
        /// Checks and throws an ArgumentException if the given array is null or contains null element
        /// </summary>
        /// <param name="array">array which needs to be checked</param>
        /// <param name="argName">name of the argument being checked, used in exception message</param>
        public static void requireArrayNotContainNull(object[] array, string argName)
        {
            requireNotNull(array, argName);
            foreach (object x in array)
                require(x != null, "required that elements of " + argName + " must NOT be null");
        }
    }
}
