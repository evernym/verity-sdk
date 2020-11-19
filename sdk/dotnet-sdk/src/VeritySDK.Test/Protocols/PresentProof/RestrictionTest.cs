using Microsoft.VisualStudio.TestTools.UnitTesting;
using VeritySDK.Protocols.PresentProof;

namespace VeritySDK.Test
{
    [TestClass]
    public class RestrictionTest
    {
        [TestMethod]
        public void buildTest()
        {
            Restriction test = RestrictionBuilder.blank()
                    .schemaId("ASDFASDF")
                    .build();

            Assert.AreEqual(test.toJson().ToString(), "{\"schema_id\": \"ASDFASDF\"}");
        }
    }
}
