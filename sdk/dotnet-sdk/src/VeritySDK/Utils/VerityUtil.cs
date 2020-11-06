using System;
using System.IO;
using System.Net.Http;
using System.Text.Json;

namespace VeritySDK
{
    /// <summary>
    /// Utilities for basic interactions with the verity-application
    /// </summary>
    public class VerityUtil
    {
        class JData
        {
            public string DID { get; set; }
            public string verKey { get; set; }
        }

        public static Did retrieveVerityPublicDid(String verityUrl)
        {
            String fullUrl = verityUrl + "/agency";

            HttpClient client = new HttpClient();
            var response = client.PostAsync(verityUrl, null).GetAwaiter().GetResult();
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
                    var msgObj = JsonSerializer.Deserialize<JData>(responseString);

                    return new Did(msgObj.DID, msgObj.verKey);
                }
                catch (JsonException e)
                {
                    throw new IOException("Invalid and unexpected data from Verity -- response -- " + responseString);
                }
            }
        }
    }
}
