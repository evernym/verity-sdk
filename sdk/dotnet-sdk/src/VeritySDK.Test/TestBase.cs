using Com.Evernym.Vdrtools.WalletApi;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System;
using System.Collections.Generic;
using System.Json;
using System.Text;
using VeritySDK.Utils;
using VeritySDK.Wallets;

namespace VeritySDK.Test
{
    public abstract class TestBase
    {
        public delegate void OnContextAction(Context context);

        public void withContext(OnContextAction action)
        {
            Context context = null;
            try
            {
                context = TestHelpers.getContext();
                action(context);
            }
            catch (Exception e)
            {
                Assert.Fail(e.Message);
            }
            finally
            {
                TestHelpers.cleanup(context);
            }
        }

    }
}
