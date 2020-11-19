using Microsoft.VisualStudio.TestTools.UnitTesting;
using Moq;
using Moq.Protected;
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
    public class TransportTests : TestBase
    {
        private HttpClient getHttpClient(byte[] content)
        {
            var handlerMock = new Mock<HttpMessageHandler>();

            var response = new HttpResponseMessage
            {
                StatusCode = HttpStatusCode.OK,
                Content = new ByteArrayContent(content),
            };

            handlerMock
                .Protected()
                .Setup<Task<HttpResponseMessage>>("SendAsync", ItExpr.IsAny<HttpRequestMessage>(), ItExpr.IsAny<CancellationToken>())
                .ReturnsAsync(response);

            var httpClient = new HttpClient(handlerMock.Object);

            return httpClient;
        }


        [TestMethod]
        public void SendSyncMessage()
        {
            withContext(context =>
            {
                var return_json = JsonObject.Parse("{\"DID\":\"CV65RFpeCtPu82hNF9i61G\",\"verKey\":\"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"}") as JsonObject;
                var return_msg = Util.packMessageForVerity(context, return_json);
                Transport t = new Transport(getHttpClient(return_msg));

                var json = JsonObject.Parse("{\"DID\":\"CV65RFpeCtPu82hNF9i61G\",\"verKey\":\"7G3LhXFKXKTMv7XGx1Qc9wqkMbwcU2iLBHL8x1JXWWC2\"}") as JsonObject;
                var message = Util.packMessageForVerity(context, json);

                var result_bytes = t.sendSyncMessage("http://localhost", message);
                var result = TestHelpers.unpackForwardMessage(context, result_bytes);


                Assert.AreEqual("CV65RFpeCtPu82hNF9i61G", result.getAsString("DID"));
            });
        }
    }
}
