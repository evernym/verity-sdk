using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Moq.Protected;
using System;
using System.IO;
using System.Json;
using System.Net;
using System.Net.Http;
using System.Threading;
using System.Threading.Tasks;
using VeritySDK.Transports;
using VeritySDK.Utils;

namespace VeritySDK.Test
{
    [TestClass]
    public class VerityUtilTest
    {
        private HttpClient getHttpClient(string content)
        {
            var handlerMock = new Mock<HttpMessageHandler>();
            var response = new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new StringContent(content),
            };

            handlerMock
                .Protected()
                .Setup<Task<HttpResponseMessage>>("SendAsync", ItExpr.IsAny<HttpRequestMessage>(), ItExpr.IsAny<CancellationToken>())
                .ReturnsAsync(response);

            var httpClient = new HttpClient(handlerMock.Object);

            return httpClient;
        }

        [TestMethod]
        public void RetrieveVerityPublicDid_canMock()
        {
            var httpClient = getHttpClient("{\"DID\":\"CV65RFpeCtPu82hNF9i61G\",\"verKey\":\"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"}");
            
            Did did = VerityUtil.retrieveVerityPublicDid("http://localhost", httpClient);

            Assert.AreEqual("CV65RFpeCtPu82hNF9i61G", did.did);
            Assert.AreEqual("7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2", did.verkey);
        }

        [TestMethod]
        public void RetrieveVerityPublicDid_exceptionWhenNon200()
        {
            Assert.ThrowsException<IOException>(() =>
            {
                var httpClient = getHttpClient("{}");

                Did did = VerityUtil.retrieveVerityPublicDid("http://localhost", httpClient);
            });
        }
    }
}
