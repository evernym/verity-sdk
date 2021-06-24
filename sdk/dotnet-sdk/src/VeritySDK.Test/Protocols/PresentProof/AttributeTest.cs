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

        [TestMethod]
        public void multipleRestrictionTest()
        {
            Restriction r1 = RestrictionBuilder.blank()
                    .credDefId("credDefId1")
                    .build();
            Restriction r2 = RestrictionBuilder.blank()
                    .credDefId("credDefId2")
                    .build();
            Attribute a = new Attribute("test", r1, r2);
            JsonArray restrictions = a.data.getAsJsonArray("restrictions");
            Assert.AreEqual(2, restrictions.Count);
            Assert.AreEqual(restrictions.ToString(), "[{\"cred_def_id\": \"credDefId1\"}, {\"cred_def_id\": \"credDefId2\"}]");
        }

        [TestMethod]
        public void namesTest()
        {
            string[] names = {"A", "B", "C"};
            Attribute a = new Attribute(names, (Restriction)null);
            Assert.AreEqual(a.data.getAsJsonArray("names").ToString(), "[\"A\", \"B\", \"C\"]");
            Assert.AreEqual(0, a.data.getAsJsonArray("restrictions").Count);
        }

        [TestMethod]
        public void namesOneRestrictionTest()
        {
            string[] names = {"A", "B", "C"};
            Restriction r = RestrictionBuilder.blank()
                .credDefId("SDFSDF")
                .build();
            Attribute a = new Attribute(names, r);
            Assert.AreEqual(a.data.getAsJsonArray("names").ToString(), "[\"A\", \"B\", \"C\"]");
            JsonArray restrictions = a.data.getAsJsonArray("restrictions");
            Assert.AreEqual(1, restrictions.Count);
            Assert.AreEqual(restrictions.ToString(), "[{\"cred_def_id\": \"SDFSDF\"}]");
        }

    }
}
