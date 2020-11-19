using System;
using System.IO;
using System.Net;

namespace VeritySDK.Sample
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("Sample \"VeritySDK .Net wrapper\" started");
            Console.WriteLine("");

            new App().Execute();

            Console.WriteLine("");
            Console.WriteLine("Sample \"VeritySDK .Net wrapper\" finished");

            Console.WriteLine("");
            Console.WriteLine("Press any key for exit");
            Console.ReadKey();
        }
    }
}
