namespace VeritySDK.Exceptions
{
    ///// <summary>
    ///// Exception class that is thrown when the wallet fails to open
    ///// </summary>
    public class WalletClosedException : WalletException
    {
        /// <summary>
        /// Constructs a new exception.
        /// </summary>
        public WalletClosedException() : base("A context wallet handle accessed after being closed") { }
    }
}
