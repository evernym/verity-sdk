using System;
using System.IO;
using System.Net.Http;
using System.Json;

namespace VeritySDK.Utils
{
    /// <summary>
    /// Utilities for basic interactions with the verity-application
    /// </summary>
    public class VerityUtil
    {
        public static Did retrieveVerityPublicDid(string verityUrl, HttpClient httpClient = null)
        {
            HttpClient client = httpClient ?? new HttpClient();

            string fullUrl = verityUrl + "/agency";
            var response = client.GetAsync(fullUrl).GetAwaiter().GetResult();

            var responseString = response.Content.ReadAsStringAsync().GetAwaiter().GetResult();

            var statusCode = (int)response.StatusCode;
            if (statusCode > 399)
            {
                throw new IOException("Request failed! - " + statusCode + " - " + responseString);
            }
            else
            {
                try
                {
                    var msgObj = JsonObject.Parse(responseString);
                    return new Did(msgObj["DID"], msgObj["verKey"]);
                }
                catch (Exception e)
                {
                    throw new IOException("Invalid and unexpected data from Verity\r\nResponse -- " + responseString + "\r\nError -- " + e.Message);
                }
            }
        }
    }
}