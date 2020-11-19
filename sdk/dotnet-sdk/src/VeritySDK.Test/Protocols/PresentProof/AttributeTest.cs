using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Json;
using VeritySDK.Protocols.PresentProof;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class AttributeTest
    {
        [TestMethod]
        public void nullTest()
        {
            Attribute r = new Attribute("test", (Restriction)null);
            Assert.AreEqual(0, r.data.getAsJsonArray("restrictions").Count);
        }

        [TestMethod]
        public void noRestrictionTest()
        {
            Attribute r = new Attribute("test");
            Assert.AreEqual(0, r.data.getAsJsonArray("restrictions").Count);
        }

        [TestMethod]
        public void oneRestrictionTest()
        {
            Restriction r = RestrictionBuilder.blank()
                    .credDefId("SDFSDF")
                    .build();
            Attribute a = new Attribute("test", r);
            JsonArray restrictions = a.data.getAsJsonArray("restrictions");
            Assert.AreEqual(1, restrictions.Count);
            Assert.AreEqual(restrictions.ToString(), "[{\"cred_def_id\": \"SDFSDF\"}]");
        }
    }
}
