namespace VeritySDK
{
    ///// <summary>
    ///// Exception class that is thrown when the wallet fails to open
    ///// </summary>
    public class WalletClosedException : WalletException
    {
        /// <summary>
        /// Constructs a new exception.
        /// </summary>
        /// <param name="message">message the given message for the cause of the exception.</param>
        public WalletClosedException() : base("A context wallet handle accessed after being closed") { }
    }

}
