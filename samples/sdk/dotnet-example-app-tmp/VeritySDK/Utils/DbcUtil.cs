using System;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Utilities for design by contract. Allows for common pre-condition and post-condition checking.
    /// </summary>
    public class DbcUtil
    {
        /// <summary>
        /// Checks and throws an IllegalArgumentException if the given object is null
        /// </summary>
        /// <param name="arg">any object that can be null</param>
        public static void requireNotNull(object arg)
        {
            requireNotNull(arg, "ARG");
        }

        /// <summary>
        /// Checks and throws an IllegalArgumentException if the given object is null
        /// </summary>
        /// <param name="arg">any object that can be null</param>
        /// <param name="argName">name of the argument being checked, used in exception message if null</param>
        public static void requireNotNull(object arg, string argName)
        {
            require(arg != null, "required that " + argName + " must NOT be null");
        }

        /// <summary>
        /// Checks that given boolean is true, throws an IllegalArgumentException if false
        /// </summary>
        /// <param name="requirement">requirement testable requirement</param>
        public static void require(bool requirement)
        {
            require(requirement, "");
        }

        /// <summary>
        /// Checks that given boolean is true, throws an IllegalArgumentException if false
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
    }
}
