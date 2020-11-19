using Microsoft.VisualStudio.TestTools.UnitTesting;
using VeritySDK.Exceptions;
using VeritySDK.Protocols;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class MessageFamilyTest
    {
        internal class TestFamily : MessageFamily
        {
            public override string qualifier() { return Util.EVERNYM_MSG_QUALIFIER; }
            public override string family() { return "testing"; }
            public override string version() { return "0.1"; }
        };

        private TestFamily testFamily = new TestFamily();

        [TestMethod]
        public void extractMessageName()
        {
            string name = testFamily.messageName("did:sov:123456789abcdefghi1234;spec/testing/0.1/test");
            Assert.AreEqual("test", name);

            name = testFamily.messageName("did:sov:123456789abcdefghi1234;spec/testing/0.1/234asdf234@$");
            Assert.AreEqual("234asdf234@$", name);
        }

        [TestMethod]
        public void invalidMessageFamily()
        {
            Assert.ThrowsException<InvalidMessageTypeException>(() =>
                testFamily.messageName("did:sov:BzCbsNYhMrjHiqZDTUASHg;spec/testing/0.1/test")
            );
        }

        [TestMethod]
        public void invalidMessageName()
        {
            Assert.ThrowsException<InvalidMessageTypeException>(() =>
                testFamily.messageName("did:sov:123456789abcdefghi1234;spec/testing/0.1/")
            );
        }

        [TestMethod]
        public void invalidMessageName2()
        {
            Assert.ThrowsException<InvalidMessageTypeException>(() =>
                testFamily.messageName("did:sov:123456789abcdefghi1234;spec/testing/0.1-test")
            );
        }
    }
}
