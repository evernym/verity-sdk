using System;
using System.Collections.Generic;
using System.Dynamic;
using System.Json;
using System.Runtime.CompilerServices;
using System.Text;

namespace VeritySDK
{
    /// <summary>
    ///  Extension for safe get values from JSON
    /// </summary>
    public static class JsonExtension
    {
        /// <summary>
        /// Get values
        /// </summary>
        /// <param name="json">JSON object</param>
        /// <param name="key">Key name</param>
        /// <returns>JSON value or null</returns>
        public static JsonValue Get(this JsonObject json, string key)
        {
            JsonValue value = null;
            json.TryGetValue(key, out value);
            return value;
        }
    }
}
