using System;

namespace VeritySDK
{
    /**
     * Utilities for design by contract. Allows for common pre-condition and post-condition checking.
     */
    public class DbcUtil
    {
        /**
         * Checks and throws an IllegalArgumentException if the given object is null
         * @param arg any object that can be null
         */
        public static void requireNotNull(object arg)
        {
            requireNotNull(arg, "ARG");
        }

        /**
         * Checks and throws an IllegalArgumentException if the given object is null
         * @param arg any object that can be null
         * @param argName name of the argument being checked, used in exception message if null
         */
        public static void requireNotNull(object arg, string argName)
        {
            require(arg != null, "required that " + argName + " must NOT be null");
        }

        /**
         * Checks that given boolean is true, throws an IllegalArgumentException if false
         * @param requirement testable requirement
         */
        public static void require(bool requirement)
        {
            require(requirement, "");
        }

        /**
         * Checks that given boolean is true, throws an IllegalArgumentException if false
         * @param requirement testable requirement
         * @param msg string that is used to build exception message
         */
        public static void require(bool requirement, string msg)
        {
            if (!requirement)
            {
                throw new ArgumentException("requirement failed: " + msg);
            }
        }
    }
}
