using Microsoft.AspNetCore.Http;
using System;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace VeritySDK.Transports
{
    /// <summary>
    /// Support for sending messages using the HTTP transport
    /// </summary>
    public class Transport
    {
        private HttpClient client { get; set; }

        /// <summary>
        /// Constructor
        /// </summary>
        public Transport()
        {
            client = new HttpClient();
        }

        /// <summary>
        /// Constructor for unit-test
        /// </summary>
        public Transport(HttpClient client)
        {
            this.client = client;
        }

        private string msgEndpointUrl(string verityUrl)
        {
            return $"{verityUrl}/agency/msg";
        }

        private HttpRequestMessage buildRequest(string url, byte[] message)
        {
            var request = new HttpRequestMessage(HttpMethod.Post, msgEndpointUrl(url));

            request.Content = new ByteArrayContent(message);
            request.Content.Headers.Add("Content-Type", "application/octet-stream");

            return request;
        }

        private HttpResponseMessage transportMessage(HttpRequestMessage request)
        {
            var response = client.PostAsync(request.RequestUri, request.Content).GetAwaiter().GetResult();
            if (response.IsSuccessStatusCode)
                return response;
            else
            {
                var s = response.Content.ReadAsStringAsync().GetAwaiter().GetResult();
                throw new IOException($"{response.ToString()} \r\n\r\n{s}\r\n");
            }
        }

        /// <summary>
        /// Send an encrypted agent message to a specified endpoint
        /// </summary>
        /// <param name="url">verityUrl the url where the message will be POSTed to</param>
        /// <param name="message">the encrypted agent message</param>

        public void sendMessage(string url, byte[] message)
        {
            var request = buildRequest(url, message);
            transportMessage(request);
        }

        /// <summary>
        /// Send an encrypted agent message to a specified endpoint
        /// </summary>
        /// <param name="url">verityUrl the url where the message will be POSTed to</param>
        /// <param name="message">the encrypted agent message</param>
        /// <returns>the byte array for answer</returns>
        public byte[] sendSyncMessage(string url, byte[] message)
        {
            var request = buildRequest(url, message);
            var response = transportMessage(request);

            return response.Content.ReadAsByteArrayAsync().GetAwaiter().GetResult();
        }
    }
}
